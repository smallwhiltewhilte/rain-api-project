package com.rain.rainapigateway;


import cn.hutool.core.net.URLDecoder;
import com.rain.rainapiclientsdk.utils.SignUtils;
import com.rain.rainapicommon.model.entity.InterfaceInfo;
import com.rain.rainapicommon.model.entity.User;
import com.rain.rainapicommon.service.InnerInterfaceInfoService;
import com.rain.rainapicommon.service.InnerUserInterfaceInfoService;
import com.rain.rainapicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");
    private static final long FIVE_MINUTES = 5 * 60;
    private static final String INTERFACE_HOST = "http://localhost:8123";
    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识" + request.getId());
        log.info("请求路径" + path);
        log.info("请求方法" + method);
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求参数" + request.getQueryParams());
        log.info("请求来源地址" + sourceAddress);
        log.info("请求来源地址" + request.getRemoteAddress());
        ServerHttpResponse response = exchange.getResponse();
        // 2. 黑白名单
       /* if (!IP_WHITE_LIST.contains(sourceAddress)) {
            return handleNoAuth(response);
        }*/
        // 3. 用户鉴权（判断 ak、sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = URLDecoder.decode(headers.getFirst("body"), StandardCharsets.UTF_8);
        // 去数据库查询是否分配给用户
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
//        if (!"wxxb".equals(accessKey)) {
//            return handleNoAuth(response);
//        }
        if (Long.parseLong(nonce) > 100000) {
            return handleNoAuth(response);
        }
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - Long.parseLong(timestamp) > FIVE_MINUTES) {
            return handleNoAuth(response);
        }
        String secretKey = invokeUser.getSecretKey();
        // 实际情况中是从数据库中查出 secretKey
        String serverSign = SignUtils.getSign(body, secretKey);
        if (sign == null || !sign.equals(serverSign)) {
            return handleNoAuth(response);
        }
        // 4. 请求的模拟接口是否存在
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }
        // 5. 判断是否有调用次数
        Long interfaceInfoId = interfaceInfo.getId();
        Long userId = invokeUser.getId();
        boolean haveInvokeCount = innerUserInterfaceInfoService.haveInvokeCount(interfaceInfoId, userId);
        if (!haveInvokeCount){
            return handleNoAuth(response);
        }
        // 6. 请求转发，调用模拟接口 + 响应日志
//        Mono<Void> filter = chain.filter(exchange);
//        return filter;
        return handleResponse(exchange, chain, interfaceInfoId, userId);
    }

    private static Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        //log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // 7. 响应成功，调用次数+1
                                try {
                                    innerUserInterfaceInfoService.deductInvokeCount(interfaceInfoId, userId);
                                } catch (Exception e) {
                                    log.error("invokeCount error", e);
                                }
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                String data = new String(content, StandardCharsets.UTF_8);//data

                                sb2.append(data);
                                // 打印日志
                                log.info("响应结果：" + data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            // 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    private static Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
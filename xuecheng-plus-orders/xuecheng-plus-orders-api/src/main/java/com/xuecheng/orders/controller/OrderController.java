package com.xuecheng.orders.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.orders.config.AlipayConfig;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import com.xuecheng.orders.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author wangzan
 * @version 1.0
 * @description 订单支付接口
 * @date 2023/2/2
 */
@Api(value = "订单支付接口", tags = "订单支付接口")
@Slf4j
@Controller
public class OrderController {
    @Autowired
    OrderService orderService;
    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @ApiOperation("生成支付二维码")
    @PostMapping("/generatepaycode")
    @ResponseBody
    public PayRecordDto generatePayCode(@RequestBody AddOrderDto addOrderDto) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String userId = user.getId();
        return orderService.createOrder(userId, addOrderDto);
    }

    @ApiOperation("扫码下单接口")
    @GetMapping("/requestpay")
    public void requestPay(String payNo, HttpServletResponse httpResponse) throws IOException, AlipayApiException {
        // 校验payNo交易号是否存在
        XcPayRecord payRecord = orderService.getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            XueChengPlusException.cast("支付记录不存在，无法支付");
        }
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGN_TYPE);
        //创建API对应的request
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
//        alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        //在公共参数中设置回跳和通知地址
        alipayRequest.setNotifyUrl("http://15fd1024.r5.vip.cpolar.cn/orders/receiveNotify");
        //填充业务参数
        alipayRequest.setBizContent("{" +
                // 商户网站唯一订单号，指定支付记录的交易号
                "    \"out_trade_no\":\"" + payRecord.getPayNo() + "\"," +
                "    \"total_amount\":" + payRecord.getTotalPrice() + "," +
                "    \"subject\":\"" + payRecord.getOrderName() + "\"," +
                "    \"product_code\":\"QUICK_WAP_WAY\"" +
                "  }");
        //调用SDK生成表单
        String form = alipayClient.pageExecute(alipayRequest).getBody();
        httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        //直接将完整的表单html输出到页面
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
    }

    // 接收支付宝支付结果通知
    @PostMapping("/receiveNotify")
    public void receiveNotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {

        PrintWriter out = response.getWriter();
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        // 验证签名
        boolean verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");
        //验证成功
        if (verify_result) {
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号

            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            //app_id
            String app_id = new String(request.getParameter("app_id").getBytes("ISO-8859-1"), "UTF-8");
            //app_id
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");

            if (AlipayConfig.SUCCESS.equals(trade_status)) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                //如果有做过处理，不执行商户的业务程序
                System.out.println("=============================支付成功=============================");
                //更新订单表的状态
                // 支付渠道标号603002
                //
                PayStatusDto payStatusDto = new PayStatusDto();
                // 支付宝支付的appId
                payStatusDto.setApp_id(app_id);
                // 支付结果
                payStatusDto.setTrade_status(trade_status);
                // 商户自己的订单号
                payStatusDto.setOut_trade_no(out_trade_no);
                // 总金额
                payStatusDto.setTotal_amount(total_amount);
                // 支付宝的订单号
                payStatusDto.setTrade_no(trade_no);
                orderService.saveAliPayStatus(payStatusDto);
            }
            out.println("success");
        } else {//验证失败
            out.println("fail");
        }
    }
}

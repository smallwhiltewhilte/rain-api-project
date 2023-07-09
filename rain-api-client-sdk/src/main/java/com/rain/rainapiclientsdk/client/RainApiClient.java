package com.rain.rainapiclientsdk.client;

import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.rain.rainapiclientsdk.model.Article;
import com.rain.rainapiclientsdk.model.Poetry;
import com.rain.rainapiclientsdk.model.User;
import org.springframework.web.socket.client.WebSocketClient;

import javax.websocket.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static com.rain.rainapiclientsdk.utils.SignUtils.getSign;


/**
 * 调用第三方接口的客户端
 *
 * @author 王赞
 */
@ClientEndpoint
public class RainApiClient {
    private String accessKey;
    private String secretKey;
    private static final String GATEWAY_HOST="http://127.0.0.1:8090";
    private static final String WEBSOCKET_GATEWAY_HOST="ws://127.0.0.1:8090";
    private final Gson gson = new Gson();
    private Consumer<String> consumer;
    public RainApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        return HttpUtil.get(GATEWAY_HOST+"/api/name/", paramMap);
    }

    public String getNameByPost(String name) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        return HttpUtil.post(GATEWAY_HOST+"/api/name/", paramMap);
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey);
//        一定不能直接发送
//        map.put("secretKey", secretKey);
        map.put("nonce", RandomUtil.randomNumbers(5));
        map.put("body", URLEncodeUtil.encode(body,StandardCharsets.UTF_8));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
        map.put("sign", getSign(body, secretKey));
        return map;
    }

    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST+"/api/name/user")
                .charset(StandardCharsets.UTF_8)
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        return httpResponse.body();
    }
    public String getVerse(Poetry poetry) {
        String json = JSONUtil.toJsonStr(poetry);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST+"/api/poetry/verse")
                .charset(StandardCharsets.UTF_8)
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        return httpResponse.body();
    }
    public void getArticle(Article article, Consumer<String> consumer){
        // 建立连接后
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String sid = UUID.randomUUID().toString();
        article.setArticleId(sid);
        try {
            Session session = container.connectToServer(WebSocketClient.class, new URI(WEBSOCKET_GATEWAY_HOST+"/article"+sid));
            session.getBasicRemote().sendText(gson.toJson(article));
            this.consumer = consumer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @OnMessage
    public void onMessage(String message) {
        if (consumer != null) {
            consumer.accept(message);
        }
    }
}

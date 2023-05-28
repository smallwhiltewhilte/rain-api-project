package com.rain.rainapiclientsdk.client;

import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.rain.rainapiclientsdk.model.Poetry;
import com.rain.rainapiclientsdk.model.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.rain.rainapiclientsdk.utils.SignUtils.getSign;


/**
 * 调用第三方接口的客户端
 *
 * @author 王赞
 */
public class RainApiClient {
    private String accessKey;
    private String secretKey;
    private static final String GATEWAY_HOST="http://127.0.0.1:8090";
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
}

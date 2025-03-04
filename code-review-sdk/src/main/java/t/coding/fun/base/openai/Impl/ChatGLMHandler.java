package t.coding.fun.base.openai.Impl;

import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import t.coding.fun.base.openai.IOpenAi;
import t.coding.fun.model.openai.ChatCompletionRequestDTO;
import t.coding.fun.model.openai.ChatCompletionSyncResponseDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* @Description: 基于GLM的对话处理 具体的可参考 https://www.bigmodel.cn/dev/api/normal-model/glm-4
* @Param:
* @return:
* @Author: tangzhaofeng
* @Date: 3/5/2025
*/
public class ChatGLMHandler implements IOpenAi {

    private final String apiHost;
    private final String apiKeySecret;

    // 过期时间；默认30分钟
    private static final long expireMillis = 30 * 60 * 1000L;


    public ChatGLMHandler(String apiHost, String apiKeySecret) {
        this.apiHost = apiHost;
        this.apiKeySecret = apiKeySecret;
    }

    // 缓存服务
    public static Cache<String, String> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(expireMillis - (60 * 1000L), TimeUnit.MILLISECONDS)
            .build();

    public static String getToken(String apiKeySecret) {
        String[] split = apiKeySecret.split("\\.");
        return getToken(split[0], split[1]);
    }


    /**
     * 对 ApiKey 进行签名
     *
     * @param apiKey    登录创建 ApiKey <a href="https://open.bigmodel.cn/usercenter/apikeys">apikeys</a>
     * @param apiSecret apiKey的后半部分 828902ec516c45307619708d3e780ae1.w5eKiLvhnLP8MtIf 取 w5eKiLvhnLP8MtIf 使用
     * @return Token
     */
    public static String getToken(String apiKey, String apiSecret) {
        // 缓存Token
        String token = cache.getIfPresent(apiKey);
        if (null != token) return token;
        // 创建Token
        Algorithm algorithm = Algorithm.HMAC256(apiSecret.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> payload = new HashMap<>();
        payload.put("api_key", apiKey);
        payload.put("exp", System.currentTimeMillis() + expireMillis);
        payload.put("timestamp", Calendar.getInstance().getTimeInMillis());
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", "HS256");
        headerClaims.put("sign_type", "SIGN");
        token = JWT.create().withPayload(payload).withHeader(headerClaims).sign(algorithm);
        cache.put(apiKey, token);
        return token;
    }



    @Override
    public ChatCompletionSyncResponseDTO handle(ChatCompletionRequestDTO requestDTO) throws IOException {
        //基于GLM 官方介绍，需要借助生成token
        String token = getToken(apiKeySecret);

        URL url = new URL(apiHost);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(requestDTO).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        //将返回的json字符串转换为ChatCompletionSyncResponseDTO对象
        return JSON.parseObject(content.toString(), ChatCompletionSyncResponseDTO.class);

    }
}

package life.interview.community.community.provider;

import com.alibaba.fastjson.JSON;
import life.interview.community.community.dto.AccessTokenDTO;
import life.interview.community.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Component
@Slf4j
public class GithubProvider {

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
//        OkHttpClient client = new OkHttpClient();

//        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
//        Request request = new Request.Builder()
//                .url("https://github.com/login/oauth/access_token")
//                .post(body)
//                .build();

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token?client_id=d869dea996eb69a1b636&client_secret=e7e90a308978e8568ceab2486385ace282d146f5&code="+accessTokenDTO.getCode()+"&redirect_uri=http://localhost:8080/callback&state=1")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            System.out.println("stirng"+string);
            String token = string.split("&")[0].split("=")[1];
            System.out.println(token);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
//            log.error("getAccessToken error,{}", accessTokenDTO, e);
        }
        return null;
    }

    public GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();
        System.out.println(accessToken);
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            /*自动将json解析为Java类对象*/
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (Exception e) {
            e.printStackTrace();
//            log.error("getUser error,{}", accessToken, e);
        }
        return null;
    }

}

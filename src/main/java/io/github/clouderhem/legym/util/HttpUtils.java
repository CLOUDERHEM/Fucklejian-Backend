package io.github.clouderhem.legym.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Aaron Yeung
 */
@Slf4j
public class HttpUtils {

    private static final OkHttpClient CLIENT = new OkHttpClient()
            .newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    public static String doGet(String url, String auth, boolean needLog) throws IOException {

        Request.Builder builder = new Request.Builder()
                .url(url);
        if (auth != null) {
            builder.addHeader("Authorization", "Bearer " + auth)
                    .addHeader("user-agent", "okhttp/4.9.3")
                    .addHeader("accept-encoding", "gzip");
        }
        Request request = builder.build();

        try (Response response = CLIENT.newCall(request).execute()) {
            String res = Objects.requireNonNull(response.body()).string();
            if (needLog) {
                log.info("url:{} | response:{}", url, res);
            }
            return res;
        }

    }

    public static String doPost(String url, String param, String auth, boolean needLog) {

        RequestBody body = RequestBody.create(param, JSON);
        Request.Builder builder = new Request.Builder().url(url);
        if (auth != null) {
            builder.addHeader("Authorization", "Bearer " + auth)
                    .addHeader("user-agent", "okhttp/4.9.3")
                    .addHeader("accept-encoding", "gzip");
        }
        Request request = builder.post(body).build();

        Call call = CLIENT.newCall(request);
        try (Response response = call.execute()) {
            String string = Objects.requireNonNull(response.body()).string();
            if (needLog) {
                log.info("url:{} | request:{} | response:{} ", url, param, string);
            }
            return string;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

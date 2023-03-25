package kr.jm.gpt.openai.sse;


import com.king.platform.net.http.HttpClient;
import com.king.platform.net.http.HttpResponse;
import com.king.platform.net.http.ResponseBodyConsumer;
import com.king.platform.net.http.netty.NettyHttpClientBuilder;
import kr.jm.gpt.openai.OpenAiApiConf;

import java.util.concurrent.CompletableFuture;

import static com.king.platform.net.http.ConfKeys.IDLE_TIMEOUT_MILLIS;
import static kr.jm.gpt.openai.OpenAiApiConf.AUTHORIZATION;
import static kr.jm.gpt.openai.OpenAiApiConf.CONTENT_TYPE;

public class OpenAiSseClient {
    private final HttpClient httpClient;

    private final OpenAiApiConf openAiApiConf;

    public OpenAiSseClient(String openaiApiKey) {
        this(new OpenAiApiConf("https://api.openai.com/v1/chat/completions", openaiApiKey));
    }

    public OpenAiSseClient(OpenAiApiConf openAiApiConf) {
        this.openAiApiConf = openAiApiConf;
        this.httpClient = new NettyHttpClientBuilder().setOption(IDLE_TIMEOUT_MILLIS, 60_000).createHttpClient();
        this.httpClient.start();
    }

    public <T> CompletableFuture<HttpResponse<T>> consumeServerSentEvent(String body,
            ResponseBodyConsumer<T> responseBodyConsumer) {
        return httpClient.createPost(this.openAiApiConf.getOpenAIUrl())
                .contentType(this.openAiApiConf.getHeaders().get(CONTENT_TYPE))
                .content(body.getBytes()).addHeader(AUTHORIZATION, this.openAiApiConf.getHeaders().get(AUTHORIZATION))
                .build(() -> responseBodyConsumer).execute();
    }
}

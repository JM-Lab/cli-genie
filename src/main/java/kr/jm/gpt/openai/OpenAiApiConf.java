package kr.jm.gpt.openai;

import lombok.Getter;

import java.util.Map;


@Getter
public class OpenAiApiConf {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "Authorization";
    private final String openAIUrl;
    private final Map<String, String> headers;

    public OpenAiApiConf(String openAiApiUrl, String openAiApiKey) {
        this.openAIUrl = openAiApiUrl;
        this.headers = buildHeaders(openAiApiKey);
    }

    private Map<String, String> buildHeaders(String openAiApiKey) {
        return Map.of(CONTENT_TYPE, "application/json", AUTHORIZATION, "Bearer " + openAiApiKey);
    }

}

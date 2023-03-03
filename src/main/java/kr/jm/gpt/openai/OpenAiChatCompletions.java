package kr.jm.gpt.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import kr.jm.gpt.GptCompletionsInterface;
import kr.jm.gpt.openai.dto.Message;
import kr.jm.gpt.openai.dto.OpenAiChatCompletionResponse;
import kr.jm.gpt.openai.dto.OpenAiChatCompletionsRequest;
import kr.jm.utils.helper.JMJson;
import kr.jm.utils.helper.JMLog;
import kr.jm.utils.http.JMHttpRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class OpenAiChatCompletions implements GptCompletionsInterface {

    private static final JMJson JmJson =
            new JMJson(new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final String openAIUrl;
    private final Map<String, String> headers;

    OpenAiChatCompletions(String openAIUrl, String openAiApiKey) {
        this.openAIUrl = openAIUrl;
        this.headers = buildHeaders(openAiApiKey);
    }

    private Map<String, String> buildHeaders(String openAiApiKey) {
        return Map.of("Content-Type", "application/json", "Authorization", "Bearer " + openAiApiKey);
    }


    public OpenAiChatCompletions(String openAiApiKey) {
        this(System.getenv().getOrDefault("OPENAI_API_URL", "https://api.openai.com/v1/chat/completions"),
                openAiApiKey);
    }

    public OpenAiChatCompletionResponse input(String prompt) {
        return input(prompt, this.headers);
    }

    public OpenAiChatCompletionResponse input(String prompt, String openAiApiKey) {
        return input(prompt, buildHeaders(openAiApiKey));
    }

    private OpenAiChatCompletionResponse input(String prompt, Map<String, String> headers) {
        return JmJson.withJsonString(requestCompletions(headers, buildCompletionsBody(prompt)),
                OpenAiChatCompletionResponse.class);
    }

    private String requestCompletions(Map<String, String> headers, String body) {
        String response = JMHttpRequester.getInstance().postResponseAsString(headers, openAIUrl, body);
        JMLog.debug(log, "requestCompletions", body, response);
        return response;
    }

    private String buildCompletionsBody(String prompt) {
        return JmJson.toJsonString(buildOpenAiRequest(prompt));
    }

    private OpenAiChatCompletionsRequest buildOpenAiRequest(String prompt) {
        return new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setMaxTokens(2048).setTemperature(0D)
                .setMessages(
                        List.of(new Message().setRole("user").setContent(prompt)));
    }

    @Override
    public String request(String prompt) {
        return input(prompt).getChoices().get(0).getMessage().getContent();
    }
}

package kr.jm.gpt.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import kr.jm.gpt.GptCompletionsInterface;
import kr.jm.gpt.openai.dto.OpenAiCompletionResponse;
import kr.jm.gpt.openai.dto.OpenAiCompletionsRequest;
import kr.jm.utils.helper.JMJson;
import kr.jm.utils.helper.JMLog;
import kr.jm.utils.http.JMHttpRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OpenAiCompletions implements GptCompletionsInterface {

    private static final JMJson JmJson =
            new JMJson(new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final String openAIUrl;
    private final Map<String, String> headers;

    OpenAiCompletions(String openAIUrl, String openAiApiKey) {
        this.openAIUrl = openAIUrl;
        this.headers = buildHeaders(openAiApiKey);
    }

    private Map<String, String> buildHeaders(String openAiApiKey) {
        return Map.of("Content-Type", "application/json", "Authorization", "Bearer " + openAiApiKey);
    }

    public OpenAiCompletions(String openAiApiKey) {
        this(System.getenv().getOrDefault("OPENAI_API_URL", "https://api.openai.com/v1/completions"),
                openAiApiKey);
    }

    @Override
    public String request(String prompt) {
        return input(prompt).getChoices().get(0).getText();
    }

    public OpenAiCompletionResponse input(String prompt) {
        return input(prompt, this.headers);
    }

    public OpenAiCompletionResponse input(String prompt, String openAiApiKey) {
        return input(prompt, buildHeaders(openAiApiKey));
    }

    private OpenAiCompletionResponse input(String prompt, Map<String, String> headers) {
        return JmJson.withJsonString(
                requestCompletions(headers, buildCompletionsBody(prompt)),
                OpenAiCompletionResponse.class);
    }

    private String requestCompletions(Map<String, String> headers, String body) {
        String response = JMHttpRequester.getInstance().postResponseAsString(headers, openAIUrl, body);
        JMLog.debug(log, "requestCompletions", body, response);
        return response;
    }

    private String buildCompletionsBody(String prompt) {
        return JmJson.toJsonString(
                new OpenAiCompletionsRequest().setModel("text-davinci-003").setMaxTokens(2000).setTemperature(0D)
                        .setPrompt(prompt));
    }

}

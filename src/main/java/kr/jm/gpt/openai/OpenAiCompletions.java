package kr.jm.gpt.openai;

import kr.jm.gpt.GptCompletionsInterface;
import kr.jm.gpt.openai.dto.OpenAiCompletionResponse;
import kr.jm.gpt.openai.dto.OpenAiCompletionsRequest;
import kr.jm.utils.helper.JMLog;
import kr.jm.utils.http.JMHttpRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAiCompletions implements GptCompletionsInterface {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final OpenAiApiConf openAiApiConf;

    public OpenAiCompletions(OpenAiApiConf openAiApiConf) {
        this.openAiApiConf = openAiApiConf;
    }

    public OpenAiCompletions(String openAiApiKey) {
        this(new OpenAiApiConf("https://api.openai.com/v1/completions", openAiApiKey));
    }

    @Override
    public String request(String prompt) {
        return input(prompt).getChoices().get(0).getText();
    }

    public OpenAiCompletionResponse input(String prompt) {
        return JmJson.withJsonString(requestCompletions(buildCompletionsBody(prompt)), OpenAiCompletionResponse.class);
    }

    private String requestCompletions(String body) {
        String response = JMHttpRequester.getInstance()
                .postResponseAsString(openAiApiConf.getHeaders(), openAiApiConf.getOpenAIUrl(), body);
        JMLog.debug(log, "requestCompletions", body, response);
        return response;
    }

    private String buildCompletionsBody(String prompt) {
        return JmJson.toJsonString(
                new OpenAiCompletionsRequest().setModel("text-davinci-003").setMaxTokens(2000).setTemperature(0D)
                        .setPrompt(prompt));
    }

}

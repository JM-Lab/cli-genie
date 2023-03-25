package kr.jm.gpt.openai;

import kr.jm.gpt.GptCompletionsInterface;
import kr.jm.gpt.openai.dto.Message;
import kr.jm.gpt.openai.dto.OpenAiChatCompletionResponse;
import kr.jm.gpt.openai.dto.OpenAiChatCompletionsRequest;
import kr.jm.utils.helper.JMLog;
import kr.jm.utils.http.JMHttpRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OpenAiChatCompletions implements GptCompletionsInterface {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final OpenAiApiConf openAiApiConf;
    private String systemPrompt;

    public OpenAiChatCompletions(OpenAiApiConf openAiApiConf) {
        this.openAiApiConf = openAiApiConf;
    }

    public OpenAiChatCompletions(String openaiApiKey) {
        this(new OpenAiApiConf("https://api.openai.com/v1/chat/completions", openaiApiKey));
    }

    @Override
    public String request(String prompt) {
        return input(prompt).getChoices().get(0).getMessage().getContent();
    }

    public OpenAiChatCompletionResponse input(String prompt) {
        return JmJson.withJsonString(requestCompletions(buildCompletionsBody(prompt)),
                OpenAiChatCompletionResponse.class);
    }

    private String requestCompletions(String body) {
        String response = JMHttpRequester.getInstance()
                .postResponseAsString(this.openAiApiConf.getHeaders(), this.openAiApiConf.getOpenAIUrl(), body);
        JMLog.debug(log, "requestCompletions", body, response);
        return response;
    }

    private String buildCompletionsBody(String prompt) {
        return JmJson.toJsonString(buildOpenAiRequest(prompt));
    }

    private OpenAiChatCompletionsRequest buildOpenAiRequest(String prompt) {
        return new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setMaxTokens(3000).setTemperature(0D)
                .setMessages(buildMessages(prompt));
    }

    private List<Message> buildMessages(String prompt) {
        List<Message> messageList = new ArrayList<>();
        Optional.ofNullable(this.systemPrompt).map(new Message().setRole("system")::setContent)
                .ifPresent(messageList::add);
        messageList.add(new Message().setRole("user").setContent(prompt));
        return messageList;
    }

    public OpenAiChatCompletions setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
        return this;
    }

}

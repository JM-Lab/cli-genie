package kr.jm.gpt.openai;

import kr.jm.gpt.openai.dto.Message;
import kr.jm.gpt.openai.dto.OpenAiChatCompletionResponse;
import kr.jm.gpt.openai.dto.OpenAiChatCompletionsRequest;
import kr.jm.gpt.openai.sse.OpenAiChatCompletionsSseConsumer;
import kr.jm.gpt.openai.sse.OpenAiSseClient;
import kr.jm.utils.exception.JMException;
import kr.jm.utils.helper.JMLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static kr.jm.gpt.GptCompletionsInterface.JmJson;

public class OpenAiChatCompletionsSse implements GptCompletionsSseInterface {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final OpenAiSseClient openAiSseClient;

    private String systemPrompt;

    public OpenAiChatCompletionsSse(OpenAiApiConf openAiApiConf) {
        this.openAiSseClient = new OpenAiSseClient(openAiApiConf);
    }

    public OpenAiChatCompletionsSse(String openAiApiKey) {
        this(new OpenAiApiConf("https://api.openai.com/v1/chat/completions", openAiApiKey));
    }

    @Override
    public String requestWithSse(String prompt,
            OpenAiChatCompletionsSseConsumer openAiChatCompletionsSseConsumer) {
        return input(buildCompletionsBody(prompt), openAiChatCompletionsSseConsumer);
    }

    private String input(String body, OpenAiChatCompletionsSseConsumer openAiChatCompletionsSseConsumer) {
        String response = getCompletionResponse(openAiChatCompletionsSseConsumer, body).getChoices().get(0).getMessage()
                .getContent();
        JMLog.debug(log, "requestCompletions", body, response);
        return response;
    }

    private OpenAiChatCompletionResponse getCompletionResponse(
            OpenAiChatCompletionsSseConsumer openAiChatCompletionsSseConsumer, String body) {
        try {
            return this.openAiSseClient.consumeServerSentEvent(body, openAiChatCompletionsSseConsumer).get().getBody();
        } catch (InterruptedException | ExecutionException e) {
            return JMException.handleExceptionAndThrowRuntimeEx(log, e, "getCompletionResponse", body);
        }
    }

    private String buildCompletionsBody(String prompt) {
        return JmJson.toJsonString(
                new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setMaxTokens(3000).setTemperature(0D)
                        .setStream(true).setMessages(buildMessages(prompt)));
    }

    private List<Message> buildMessages(String prompt) {
        List<Message> messageList = new ArrayList<>();
        Optional.ofNullable(this.systemPrompt).map(new Message().setRole("system")::setContent)
                .ifPresent(messageList::add);
        messageList.add(new Message().setRole("user").setContent(prompt));
        return messageList;
    }

    public OpenAiChatCompletionsSse setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
        return this;
    }
}

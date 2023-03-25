package kr.jm.gpt.openai.sse;

import com.king.platform.net.http.ResponseBodyConsumer;
import kr.jm.gpt.openai.dto.ChatChoice;
import kr.jm.gpt.openai.dto.Message;
import kr.jm.gpt.openai.dto.OpenAiChatCompletionResponse;
import kr.jm.gpt.openai.dto.sse.ChoicesItem;
import kr.jm.gpt.openai.dto.sse.OpenAiSseData;
import kr.jm.utils.JMOptional;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class OpenAiChatCompletionsSseConsumer implements ResponseBodyConsumer<OpenAiChatCompletionResponse> {
    private StringBuilder tempMessageBuilder;

    private final Consumer<String> dataConsumer;

    private OpenAiChatCompletionResponse openAiChatCompletionResponse;

    private final OpenAiSseDataConsumer openAiSseDataConsumer;

    public OpenAiChatCompletionsSseConsumer(Consumer<String> dataConsumer) {
        this.dataConsumer = dataConsumer;
        this.openAiSseDataConsumer = new OpenAiSseDataConsumer(this::handleOpenAiSseData);
    }

    @Override
    public void onBodyStart(String contentType, String charset, long contentLength) throws Exception {
        this.openAiSseDataConsumer.onBodyStart(contentType, charset, contentLength);
        this.openAiChatCompletionResponse = new OpenAiChatCompletionResponse();
        this.tempMessageBuilder = new StringBuilder();
    }

    @Override
    public void onReceivedContentPart(ByteBuffer buffer) throws Exception {
        this.openAiSseDataConsumer.onReceivedContentPart(buffer);
    }

    private void handleOpenAiSseData(OpenAiSseData openAiSseData) {
        ChoicesItem choicesItem = openAiSseData.getChoices().get(0);
        if (!"stop".equals(choicesItem.getFinishReason()))
            JMOptional.getOptional(choicesItem.getDelta(), "content").ifPresentOrElse(
                    this::appendPart, () -> JMOptional.getOptional(choicesItem.getDelta(), "role")
                            .ifPresent(role -> initRole(openAiSseData, role)));
    }

    private void initRole(OpenAiSseData openAiSseData, String role) {
        if (Objects.isNull(this.openAiChatCompletionResponse.getChoices()))
            initCompletionResopnse(openAiSseData);
        else {
            completeMessage();
            this.tempMessageBuilder = new StringBuilder();
        }
        this.openAiChatCompletionResponse.getChoices()
                .add(new ChatChoice().setMessage(new Message().setRole(role)));
    }

    private void initCompletionResopnse(OpenAiSseData openAiSseData) {
        this.openAiChatCompletionResponse.setChoices(new ArrayList<>()).setId(openAiSseData.getId())
                .setObject(openAiSseData.getObject()).setCreated(openAiSseData.getCreated())
                .setModel(openAiSseData.getModel());
    }

    private void appendPart(String content) {
        tempMessageBuilder.append(content);
        dataConsumer.accept(content);
    }

    private void completeMessage() {
        this.openAiChatCompletionResponse.getChoices()
                .get(this.openAiChatCompletionResponse.getChoices().size() - 1)
                .getMessage().setContent(this.tempMessageBuilder.toString());
    }

    @Override
    public void onCompletedBody() throws Exception {
        completeMessage();
    }

    @Override
    public OpenAiChatCompletionResponse getBody() {
        return this.openAiChatCompletionResponse;
    }

}

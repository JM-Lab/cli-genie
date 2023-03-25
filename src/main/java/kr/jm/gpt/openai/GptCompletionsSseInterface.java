package kr.jm.gpt.openai;

import kr.jm.gpt.openai.sse.OpenAiChatCompletionsSseConsumer;

import java.util.concurrent.ExecutionException;

public interface GptCompletionsSseInterface {
    String requestWithSse(String prompt,
            OpenAiChatCompletionsSseConsumer openAiChatCompletionsSseConsumer) throws
            ExecutionException, InterruptedException;
}

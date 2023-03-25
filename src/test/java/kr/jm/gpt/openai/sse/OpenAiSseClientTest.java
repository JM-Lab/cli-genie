package kr.jm.gpt.openai.sse;

import com.king.platform.net.http.ResponseBodyConsumer;
import kr.jm.gpt.openai.dto.Message;
import kr.jm.gpt.openai.dto.OpenAiChatCompletionResponse;
import kr.jm.gpt.openai.dto.OpenAiChatCompletionsRequest;
import kr.jm.utils.JMOptional;
import kr.jm.utils.JMString;
import kr.jm.utils.helper.JMJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

// IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
class OpenAiSseClientTest {
    private String openAiKey;


    @BeforeEach
    void setUp() {
        this.openAiKey = JMOptional.getOptional(System.getenv("OPENAI_API_KEY"))
                .orElseThrow(() -> new RuntimeException("The OPENAI_API_KEY environment variable is not set."));
    }

    @Disabled
    @Test
    void consumeServerSentEvent() throws ExecutionException, InterruptedException {
        OpenAiSseClient openAiSseClient = new OpenAiSseClient(this.openAiKey);
        StringBuilder resultBuilder = new StringBuilder();
        OpenAiChatCompletionsSseConsumer openAiChatCompletionsSseConsumer =
                new OpenAiChatCompletionsSseConsumer(part -> {
                    System.out.print(part);
                    resultBuilder.append(part);
                });
        OpenAiChatCompletionResponse responseBody =
                openAiSseClient.consumeServerSentEvent(JMJson.getInstance().toJsonString(
                                        new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setStream(true)
                                                .setMessages(List.of(new Message().setRole("user").setContent("인공지능 공부하는 법" +
                                                        " " +
                                                        "자세히 알려줘")))),
                                openAiChatCompletionsSseConsumer)
                        .get().getBody();
        System.out.println(responseBody);
        assertEquals(responseBody.getChoices().get(0).getMessage().getContent(), resultBuilder.toString());
    }

    @Disabled
    @Test
    void consumeServerSentEventRaw() throws ExecutionException, InterruptedException {
        OpenAiSseClient openAiSseClient = new OpenAiSseClient(this.openAiKey);

        StringResponseBodyConsumer responseBodyConsumer = new StringResponseBodyConsumer();
        String responseBody = openAiSseClient.consumeServerSentEvent(JMJson.getInstance().toJsonString(
                                new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setStream(true)
                                        .setMessages(List.of(new Message().setRole("user").setContent("인공지능 공부하는 법 자세히 알려줘")))),
                        responseBodyConsumer)
                .get().getBody();
        System.out.println(responseBodyConsumer.getBody());
        assertEquals(responseBody, JMString.joiningWith("", responseBodyConsumer.getParts()));
//        JMJson.getInstance().toJsonFile(responseBodyConsumer.getParts(),
//                Paths.get("src", "test", "resources", "streamResponses.json").toFile());
    }


    public class StringResponseBodyConsumer implements ResponseBodyConsumer<String> {
        private Charset charset;

        private List<String> parts;

        private StringBuilder completedBodyBuilder;

        private String contentType;

        private long contentLength;

        @Override
        public void onBodyStart(String contentType, String charset, long contentLength) throws Exception {
            this.contentType = contentType;
            this.charset = Charset.forName(charset);
            this.contentLength = contentLength;
            this.completedBodyBuilder = new StringBuilder();
            this.parts = new ArrayList<>();
        }

        @Override
        public void onReceivedContentPart(ByteBuffer buffer) throws Exception {
            String part = Charset.defaultCharset().decode(buffer).toString();
            parts.add(part);
            completedBodyBuilder.append(part);
            System.out.print(part);
        }

        @Override
        public void onCompletedBody() throws Exception {

        }

        @Override
        public String getBody() {
            return this.completedBodyBuilder.toString();
        }

        public Charset getCharset() {
            return charset;
        }

        public List<String> getParts() {
            return parts;
        }

        public String getContentType() {
            return contentType;
        }

        public long getContentLength() {
            return contentLength;
        }

    }
}
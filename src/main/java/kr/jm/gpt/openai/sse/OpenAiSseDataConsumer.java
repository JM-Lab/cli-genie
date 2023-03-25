package kr.jm.gpt.openai.sse;

import com.king.platform.net.http.ResponseBodyConsumer;
import kr.jm.gpt.openai.dto.sse.OpenAiSseData;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static kr.jm.gpt.GptCompletionsInterface.JmJson;

public class OpenAiSseDataConsumer implements ResponseBodyConsumer<List<String>> {


    private static final int DATA_BEGIN_INDEX = "data: ".length();

    private final Consumer<OpenAiSseData> openAiSseDataConsumer;

    private List<String> rawDataList;

    public OpenAiSseDataConsumer(Consumer<OpenAiSseData> openAiSseDataConsumer) {
        this.openAiSseDataConsumer = openAiSseDataConsumer;
    }

    @Override
    public void onBodyStart(String contentType, String charset, long contentLength) throws Exception {
        this.rawDataList = new ArrayList<>();
    }

    @Override
    public void onReceivedContentPart(ByteBuffer buffer) throws Exception {
        onReceivedContentPart(Charset.defaultCharset().decode(buffer).toString());
    }

    void onReceivedContentPart(String originPart) {
        this.rawDataList.add(originPart);
        for (String data : originPart.split("\n\n"))
            if (!data.isBlank() && !data.endsWith("[DONE]"))
                handlePart(data.substring(DATA_BEGIN_INDEX));
    }

    private void handlePart(String part) {
        this.openAiSseDataConsumer.accept(JmJson.withJsonString(part, OpenAiSseData.class));
    }

    @Override
    public void onCompletedBody() throws Exception {
    }

    @Override
    public List<String> getBody() {
        return this.rawDataList;
    }

}

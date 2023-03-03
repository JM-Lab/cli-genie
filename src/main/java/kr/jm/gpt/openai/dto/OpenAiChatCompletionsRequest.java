package kr.jm.gpt.openai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Accessors(chain = true)
@Data
public class OpenAiChatCompletionsRequest {
    private String model;
    private List<Message> messages;
    private Double temperature;
    private Double topP;
    private Integer n;
    private Boolean stream;
    private List<String> stop;
    private Integer maxTokens;
    private Double presencePenalty;
    private Double frequencyPenalty;
    private Map<String, Integer> logitBias;
    private String user;

}

package kr.jm.gpt.openai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Accessors(chain = true)
@Data
public class OpenAiCompletionsRequest {
    String model;
    String prompt;
    Integer maxTokens;
    Double temperature;
    Double topP;
    Integer n;
    Boolean stream;
    Integer logprobs;
    Boolean echo;
    List<String> stop;
    Double presencePenalty;
    Double frequencyPenalty;
    Integer bestOf;
    Map<String, Integer> logitBias;
    String user;

}

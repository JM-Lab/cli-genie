package kr.jm.gpt.openai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class CompletionChoice {
    String finishReason;
    Integer index;
    String text;
    Integer logprobs;
}

package kr.jm.gpt.openai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class OpenAiChatCompletionResponse {

    private List<ChatChoice> choices;
    private Long created;
    private String id;
    private String object;
    private String model;
    private Usage usage;

}

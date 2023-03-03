
package kr.jm.gpt.openai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class Usage {

    private Long completionTokens;
    private Long promptTokens;
    private Long totalTokens;

}

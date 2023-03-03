
package kr.jm.gpt.openai.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class Message {

    private String content;
    private String role;

}

package kr.jm.gpt.openai.dto.sse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ChoicesItem {
    @JsonProperty("finish_reason")
    private Object finishReason;
    private Map<String, String> delta;
    private Integer index;
}
package kr.jm.gpt;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Accessors(chain = true)
@Data
public class CliOptionsPrompt {
    private Set<String> options;
    private String prompt;
}

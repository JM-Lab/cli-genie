package kr.jm.gpt.token;

import lombok.Value;

import java.util.List;

@Value
public class TokenAnalysis {
    String prompt;
    List<Integer> tokenIds;
    List<String> readableParts;
    List<Integer> partTokenCounts;

    public int getPromptLength() {
        return this.prompt.length();
    }

    public int getTokenCount() {
        return this.tokenIds.size();
    }
}

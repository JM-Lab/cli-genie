package kr.jm.gpt.token;

import lombok.Value;

import java.util.List;

@Value
public class TokenAnalysis {
    private String prompt;
    private int tokenCount;
    private List<Integer> tokenIds;
    private List<String> readableParts;
    private List<Integer> partTokenCounts;
}

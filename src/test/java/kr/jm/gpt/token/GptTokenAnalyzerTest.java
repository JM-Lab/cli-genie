package kr.jm.gpt.token;

import com.knuddels.jtokkit.api.EncodingType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.stream.Collectors;

class GptTokenAnalyzerTest {

    private static final GptTokenAnalyzer TokenAnalyzer = new GptTokenAnalyzer(EncodingType.CL100K_BASE);

    @ParameterizedTest
    @CsvFileSource(resources = "/cl100k_base_encodings.csv", numLinesToSkip = 1, maxCharsPerColumn = 1_000_000)
    void analysisTokenTest(final String prompt) {
        TokenAnalysis tokenAnalysis = TokenAnalyzer.analysis(prompt);
        System.out.println(tokenAnalysis);
        Assertions.assertEquals(prompt, tokenAnalysis.getPrompt());
        Assertions.assertEquals(prompt,
                tokenAnalysis.getReadableParts().stream().collect(Collectors.joining()));
        Assertions.assertEquals(tokenAnalysis.getTokenIds().size(), tokenAnalysis.getTokenCount());
        Assertions.assertEquals(tokenAnalysis.getTokenCount(),
                tokenAnalysis.getPartTokenCounts().stream().mapToInt(Integer::intValue).sum());
        Assertions.assertEquals(tokenAnalysis.getPartTokenCounts().size(),
                tokenAnalysis.getReadableParts().size());
    }
}
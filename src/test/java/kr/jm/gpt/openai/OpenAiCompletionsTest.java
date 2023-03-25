package kr.jm.gpt.openai;

import kr.jm.gpt.openai.dto.OpenAiCompletionResponse;
import kr.jm.utils.JMOptional;
import kr.jm.utils.enums.OS;
import kr.jm.utils.http.JMHttpRequester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

// IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
@Disabled
class OpenAiCompletionsTest {

    OpenAiCompletions openAiCompletions;
    String openAiKey;

    @BeforeEach
    void setUp() {
        this.openAiKey = JMOptional.getOptional(System.getenv("OPENAI_API_KEY"))
                .orElseThrow(() -> new RuntimeException("The OPENAI_API_KEY environment variable is not set."));
    }

    @Test
    void completions() {
        String responseAsString = JMHttpRequester.getInstance()
                .postResponseAsString(
                        Map.of("Content-Type", "application/json", "Authorization", "Bearer " + this.openAiKey),
                        "https://api.openai.com/v1/completions", "{\n" +
                                "  \"model\": \"text-davinci-003\",\n" +
                                "  \"prompt\": \"Say this is a test\",\n" +
                                "  \"max_tokens\": 7,\n" +
                                "  \"temperature\": 0,\n" +
                                "  \"top_p\": 1,\n" +
                                "  \"n\": 1,\n" +
                                "  \"stream\": false,\n" +
                                "  \"logprobs\": null\n" +
                                "}\n");
        System.out.println(responseAsString);
    }

    @Test
    void testCompletions() {
        this.openAiCompletions = new OpenAiCompletions(this.openAiKey);
        String prompt =
                "Platform: " + OS.getOsName() + OS.getLineSeparator() + "Version: " + OS.getOsVersion() +
                        OS.getLineSeparator() + OS.getLineSeparator();
        System.out.println(prompt);
        prompt += "Generate a shell command to " + "10번 반복하면서 파일 안의 글 찾기";
        System.out.println(prompt);
        OpenAiCompletionResponse completionsResult = openAiCompletions.input(prompt);
        System.out.println(completionsResult);
        Assertions.assertEquals("\n\nfor i in {1..10}; do grep -i \"text to find\" filename; done",
                completionsResult.getChoices().get(0).getText());

        prompt += "Generate a shell command to " + "10번 반복하면서 파일 안의 글 찾기" + " and give me an example";
        System.out.println(prompt);
        completionsResult = openAiCompletions.input(prompt);
        System.out.println(completionsResult);
        Assertions.assertEquals("\n\nCommand:\n" +
                        "for i in {1..10}; do grep \"text to search\" filename; done\n\n" +
                        "Example:\n" +
                        "for i in {1..10}; do grep \"Hello World\" test.txt; done",
                completionsResult.getChoices().get(0).getText());
    }
}
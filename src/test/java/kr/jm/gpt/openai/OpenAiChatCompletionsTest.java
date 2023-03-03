package kr.jm.gpt.openai;

import kr.jm.gpt.openai.dto.OpenAiChatCompletionResponse;
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
class OpenAiChatCompletionsTest {

    OpenAiChatCompletions openAiChatCompletions;
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
                        "https://api.openai.com/v1/chat/completions", "{\n" +
                                "  \"model\": \"gpt-3.5-turbo\",\n" +
                                "  \"messages\": [{\"role\": \"user\", \"content\": \"Hello!\"}]\n" +
                                "}");
        System.out.println(responseAsString);
    }

    @Test
    void testCompletions() {
        this.openAiChatCompletions =
                new OpenAiChatCompletions("https://api.openai.com/v1/chat/completions", this.openAiKey);
        String context =
                "Platform: " + OS.getOsName() + OS.getLineSeparator() + "Version: " + OS.getOsVersion() +
                        OS.getLineSeparator() + "Do Not: explanations" + OS.getLineSeparator();
        System.out.println(context);
        String prompt = context + "Generate a shell command to " + "10번 반복하면서 파일 안의 글 찾기";
        System.out.println(prompt);
        OpenAiChatCompletionResponse completionsResult = openAiChatCompletions.input(prompt);
        System.out.println(completionsResult);
        Assertions.assertEquals("\n\nfor i in {1..10}; do grep \"찾을 글\" 파일명.txt; done",
                completionsResult.getChoices().get(0).getMessage().getContent());

        prompt = context + "Generate a shell command to " + "10번 반복하면서 파일 안의 글 찾기" + " and give me an example";
        System.out.println(prompt);
        completionsResult = openAiChatCompletions.input(prompt);
        System.out.println(completionsResult);
        Assertions.assertEquals("\n" +
                        "\n" +
                        "The shell command to repeat 10 times and search for text within a file is:\n" +
                        "\n" +
                        "```\n" +
                        "for i in {1..10}; do grep \"text_to_search\" /path/to/file.txt; done\n" +
                        "```\n" +
                        "\n" +
                        "Example:\n" +
                        "\n" +
                        "If you want to search for the word \"apple\" in a file named \"fruits.txt\" located in the Documents folder, the command would be:\n" +
                        "\n" +
                        "```\n" +
                        "for i in {1..10}; do grep \"apple\" ~/Documents/fruits.txt; done\n" +
                        "```\n" +
                        "\n" +
                        "This will search for the word \"apple\" in the file \"fruits.txt\" 10 times.",
                completionsResult.getChoices().get(0).getMessage().getContent());
    }
}
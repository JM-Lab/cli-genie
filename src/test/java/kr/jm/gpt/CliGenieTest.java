package kr.jm.gpt;

import kr.jm.gpt.openai.OpenAiCompletions;
import kr.jm.utils.JMOptional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

// IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
//@Disabled
class CliGenieTest {
    static PrintStream previousConsole;
    static ByteArrayOutputStream newConsole;
    static String openAiKey;

    @BeforeAll
    static void beforeAll() {
        openAiKey = JMOptional.getOptional(System.getenv("OPENAI_API_KEY"))
                .orElseThrow(() -> new RuntimeException("The OPENAI_API_KEY environment variable is not set."));
        previousConsole = System.out;
        newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));
    }

    @AfterAll
    static void afterAll() {
        System.setOut(previousConsole);
    }

    @Test
    void start() {
        CliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 법");
        Assertions.assertEquals("sed -i '' 's/abc/cba/g' test.txt\n", newConsole.toString());
        previousConsole.println(newConsole);

        CliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 명령 3개");
        Assertions.assertEquals("sed -i '' 's/abc/cba/g' test.txt\n" +
                "1. Using sed command:\n" +
                "sed -i '' 's/abc/cba/g' test.txt\n" +
                "\n" +
                "2. Using awk command:\n" +
                "awk '{gsub(/abc/,\"cba\")}1' test.txt > temp && mv temp test.txt\n" +
                "\n" +
                "3. Using perl command:\n" +
                "perl -pi -e 's/abc/cba/g' test.txt\n", newConsole.toString());
        previousConsole.println(newConsole);
    }

    @Test
    void buildPrompt() {
        String prompt = new CliGenie(new OpenAiCompletions(openAiKey)).buildPrompt("플랫폼 이름과 버전");
        Assertions.assertEquals("Platform: Mac OS X\n" +
                "Version: 10.16\n" +
                "Do Not: explanations\n" +
                "Generate a shell command to 플랫폼 이름과 버전", prompt);
        prompt = new CliGenie(new OpenAiCompletions(openAiKey)).buildPrompt("플랫폼 이름과 버전 알수 있는 예를 3개 보여줘");
        Assertions.assertEquals("Platform: Mac OS X\n" +
                "Version: 10.16\n" +
                "Do Not: explanations\n" +
                "Generate a shell command to 플랫폼 이름과 버전 알수 있는 예를 3개 보여줘", prompt);
    }
}
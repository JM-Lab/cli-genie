package kr.jm.gpt;

import kr.jm.gpt.openai.OpenAiCompletions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

// IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
@ExtendWith(MockitoExtension.class)
class CliGenieTest {
    private static final String CONTEXT = "CONTEXT";
    static PrintStream previousConsole;
    static ByteArrayOutputStream newConsole;
    static String openAiKey;
    @Mock
    private GptCompletionsInterface gptCompletions;
    @InjectMocks
    private CliGenie cliGenie;

    @BeforeAll
    static void beforeAll() {
        previousConsole = System.out;
        newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));
    }

    @AfterAll
    static void afterAll() {
        System.setOut(previousConsole);
    }

    // IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
    @Disabled
    @Test
    void start() {
        cliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 법");
        Assertions.assertEquals("sed -i '' 's/abc/cba/g' test.txt\n", newConsole.toString());
        previousConsole.println(newConsole);

        CliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 명령 3개");
        previousConsole.println(newConsole);
        Assertions.assertEquals("sed -i '' 's/abc/cba/g' test.txt\n" +
                "1. sed -i '' 's/abc/cba/g' test.txt\n" +
                "2. perl -pi -e 's/abc/cba/g' test.txt\n" +
                "3. awk '{gsub(/abc/,\"cba\")}1' test.txt > temp && mv temp test.txt", newConsole.toString());
    }

    // IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
    @Disabled
    @Test
    void startWithMock() {
        Mockito.when(gptCompletions.request(ArgumentMatchers.anyString()))
                .thenReturn("sed -i '' 's/abc/cba/g' test.txt");
        cliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 법\n");
        previousConsole.println(newConsole);
        Assertions.assertEquals("sed -i '' 's/abc/cba/g' test.txt\n", newConsole.toString());
    }

    // IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
    @Disabled
    @Test
    void start2WithMock() {
        Mockito.when(gptCompletions.request(ArgumentMatchers.anyString()))
                .thenReturn("sed -i '' 's/abc/cba/g' test.txt\n" +
                        "1. sed -i '' 's/abc/cba/g' test.txt\n" +
                        "2. perl -pi -e 's/abc/cba/g' test.txt\n" +
                        "3. awk '{gsub(/abc/,\"cba\")}1' test.txt > temp && mv temp test.txt");
        cliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 명령 3개");
        previousConsole.println(newConsole);
        Assertions.assertEquals("sed -i '' 's/abc/cba/g' test.txt\n" +
                "1. sed -i '' 's/abc/cba/g' test.txt\n" +
                "2. perl -pi -e 's/abc/cba/g' test.txt\n" +
                "3. awk '{gsub(/abc/,\"cba\")}1' test.txt > temp && mv temp test.txt", newConsole.toString());
    }

    @Test
    @SetEnvironmentVariable(key = "OPENAI_API_KEY", value = "testKey")
    void buildPrompt() {
        String prompt = new CliGenie(new OpenAiCompletions(openAiKey)).buildPrompt("플랫폼 이름과 버전");
        Assertions.assertEquals("Platform: Mac OS X\n" +
                "Version: 10.16\n" +
                "Do Not: explanations\n" +
                "Generate a shell command or recommendation to 플랫폼 이름과 버전", prompt);
        prompt = new CliGenie(new OpenAiCompletions(openAiKey)).buildPrompt("플랫폼 이름과 버전 알수 있는 예를 3개 보여줘");
        Assertions.assertEquals("Platform: Mac OS X\n" +
                "Version: 10.16\n" +
                "Do Not: explanations\n" +
                "Generate a shell command or recommendation to 플랫폼 이름과 버전 알수 있는 예를 3개 보여줘", prompt);
    }

}
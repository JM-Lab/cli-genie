package kr.jm.gpt;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static kr.jm.gpt.CliGenieCommandLineTest.HELP;

// IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
@ExtendWith(MockitoExtension.class)
class CliGenieTest {
    static PrintStream previousConsole;
    static ByteArrayOutputStream newConsole;
    static String openAiKey;

    @BeforeEach
    void beforeEach() {
        previousConsole = System.out;
        newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));
    }

    @AfterEach
    void afterEach() {
        System.setOut(previousConsole);
    }

    // IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
    @Disabled
    @Test
    void start() {
        CliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 법");
        Assertions.assertEquals(
                "sed -i '' 's/abc/cba/g' test.txt\n\nPaste: Command + V " +
                        "(MacOS).",
                newConsole.toString().trim());
        previousConsole.println(newConsole);

        CliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 명령 3개");
        previousConsole.println(newConsole);
        Assertions.assertEquals("sed -i '' 's/abc/cba/g' test.txt\n" +
                "\n" +
                "Paste: Command + V (MacOS).\n" +
                "1. Using sed command:\n" +
                "sed -i '' 's/abc/cba/g' test.txt\n" +
                "\n" +
                "2. Using awk command:\n" +
                "awk '{gsub(/abc/,\"cba\")}1' test.txt > temp && mv temp test.txt\n" +
                "\n" +
                "3. Using perl command:\n" +
                "perl -pi -e 's/abc/cba/g' test.txt\n" +
                "\n" +
                "Paste: Command + V (MacOS).", newConsole.toString().trim());
    }

    // IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
    @Disabled
    @Test
    void startWithoutClipboard() throws IOException, UnsupportedFlavorException {
        String expected = "sed -i '' 's/abc/cba/g' test.txt";
        CliGenie.main("-n", "test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 법");
        previousConsole.println(newConsole);
        previousConsole.println("Clipboard Data: " + Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null)
                .getTransferData(DataFlavor.stringFlavor));

        Assertions.assertNotEquals(expected, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null)
                .getTransferData(DataFlavor.stringFlavor));
        Assertions.assertEquals(expected, newConsole.toString().trim());
    }

    // IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
    @Disabled
    @Test
    void startWithClipboard() throws IOException, UnsupportedFlavorException {
        String expected = "1. Using sed command:\n" +
                "sed -i '' 's/abc/cba/g' test.txt\n" +
                "\n" +
                "2. Using awk command:\n" +
                "awk '{gsub(/abc/,\"cba\")}1' test.txt > temp && mv temp test.txt\n" +
                "\n" +
                "3. Using perl command:\n" +
                "perl -pi -e 's/abc/cba/g' test.txt";

        CliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 명령 3개");
        previousConsole.println(newConsole);

        previousConsole.println("Clipboard Data: " + Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null)
                .getTransferData(DataFlavor.stringFlavor));

        Assertions.assertEquals(expected, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null)
                .getTransferData(DataFlavor.stringFlavor));
        Assertions.assertEquals(expected + "\n\nPaste: Command + V " +
                        "(MacOS).",
                newConsole.toString().trim());
    }

    @Test
    void startAsk() throws IOException, UnsupportedFlavorException {
        String expected = "Java 설치 명령어는 다음과 같습니다:\n" +
                "\n" +
                "```\n" +
                "brew install java\n" +
                "``` \n" +
                "\n" +
                "이 명령어는 Homebrew 패키지 매니저를 사용하여 Java를 설치합니다. Homebrew가 설치되어 있지 않은 경우, 먼저 Homebrew를 설치해야 합니다.";

        CliGenie.main("java 설치 명령어");
        previousConsole.println(newConsole);
        Assertions.assertEquals(expected + "\n\nPaste: Command + V (MacOS).",
                newConsole.toString().trim());
    }

    @Test
    @SetEnvironmentVariable(key = "OPENAI_API_KEY", value = "testKey")
    void emptyTest() {
        CliGenie.main();
        previousConsole.println(newConsole);
        Assertions.assertEquals(HELP, newConsole.toString().trim());
    }

    @Test
    @SetEnvironmentVariable(key = "OPENAI_API_KEY", value = "testKey")
    void emptyTest2() {
        CliGenie.main("-n");
        previousConsole.println(newConsole);
        Assertions.assertEquals(HELP, newConsole.toString().trim());
    }

    @Test
    @SetEnvironmentVariable(key = "OPENAI_API_KEY", value = "testKey")
    void helpTest() {
        CliGenie.main("-n", "-h", "alsjkdflk");
        previousConsole.println(newConsole);
        Assertions.assertEquals(HELP, newConsole.toString().trim());
    }

    @Test
    @SetEnvironmentVariable(key = "OPENAI_API_KEY", value = "testKey")
    void buildPrompt() {
        String prompt = new CliGenie().buildPrompt("플랫폼 이름과 버전");
        Assertions.assertEquals("Platform: Mac OS X\n" +
                "Version: 10.16\n" +
                "Do Not: explanations\n" +
                "Generate a shell command or recommendation to 플랫폼 이름과 버전", prompt);
        prompt = new CliGenie().buildPrompt("플랫폼 이름과 버전 알수 있는 예를 3개 보여줘");
        Assertions.assertEquals("Platform: Mac OS X\n" +
                "Version: 10.16\n" +
                "Do Not: explanations\n" +
                "Generate a shell command or recommendation to 플랫폼 이름과 버전 알수 있는 예를 3개 보여줘", prompt);
    }

}
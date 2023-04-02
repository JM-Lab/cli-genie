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
                "sed -i '' 's/abc/cba/g' test.txt\n\nPaste: Command + V (MacOS).",
                newConsole.toString().trim());
        previousConsole.println(newConsole);

        CliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 명령 3개");
        previousConsole.println(newConsole);
        Assertions.assertEquals("sed -i '' 's/abc/cba/g' test.txt\n" +
                "\n" +
                "Paste: Command + V (MacOS).\n" +
                "1. sed -i '' 's/abc/cba/g' test.txt\n" +
                "2. perl -pi -e 's/abc/cba/g' test.txt\n" +
                "3. awk '{gsub(/abc/, \"cba\")}1' test.txt > temp && mv temp test.txt\n" +
                "\n" +
                "Paste: Command + V (MacOS).", newConsole.toString().trim());
    }

    // IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
    @Disabled
    @Test
    void startWithGeneral() {
        CliGenie.main("-g", "test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 법");
        Assertions.assertEquals(
                "파일을 열어서 텍스트 에디터에서 \"abc\"를 \"cba\"로 바꾸는 것이 가장 간단한 방법입니다. 만약 파일이 매우 크거나 여러 개의 파일에서 바꾸어야 하는 경우, 스크립트를 사용하여 자동으로 바꿀 수도 있습니다. 예를 들어, Python 스크립트를 사용하여 다음과 같이 작성할 수 있습니다.\n" +
                        "\n" +
                        "```python\n" +
                        "with open('test.txt', 'r') as file:\n" +
                        "    data = file.read()\n" +
                        "\n" +
                        "data = data.replace('abc', 'cba')\n" +
                        "\n" +
                        "with open('test.txt', 'w') as file:\n" +
                        "    file.write(data)\n" +
                        "```\n" +
                        "\n" +
                        "이 스크립트는 'test.txt' 파일을 열어서 파일 내용을 읽은 다음, 'abc'를 'cba'로 바꾸고 다시 파일에 쓰는 것입니다. 이 스크립트를 실행하면 파일 내용이 변경됩니다.\n" +
                        "\n" +
                        "Paste: Command + V (MacOS).",
                newConsole.toString().trim());
        previousConsole.println(newConsole);
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
        String expected = "sed -i '' 's/abc/cba/g' test.txt\n" +
                "perl -pi -e 's/abc/cba/g' test.txt\n" +
                "awk '{gsub(/abc/,\"cba\")}1' test.txt > temp && mv temp test.txt";

        CliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 명령 3개");
        previousConsole.println(newConsole);

        previousConsole.println("Clipboard Data: " + Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null)
                .getTransferData(DataFlavor.stringFlavor));

        Assertions.assertEquals(expected, Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null)
                .getTransferData(DataFlavor.stringFlavor));
        Assertions.assertEquals(expected + "\n\nPaste: Command + V (MacOS).",
                newConsole.toString().trim());
    }

    @Disabled
    @Test
    void startAsk() {
        String expected = "java 설치 명령어: brew install java";

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
        String prompt = new CliGenie().buildPromptWithCondition("플랫폼 이름과 버전");
        Assertions.assertEquals("Platform: Mac OS X\n" +
                "Version: 10.16\n" +
                "Do Not: explanations\n" +
                "Generate a shell command or recommendation to the following ASK\n" +
                "ASK: 플랫폼 이름과 버전", prompt);
        prompt = new CliGenie().buildPromptWithCondition("플랫폼 이름과 버전 알수 있는 예를 3개 보여줘");
        Assertions.assertEquals("Platform: Mac OS X\n" +
                "Version: 10.16\n" +
                "Do Not: explanations\n" +
                "Generate a shell command or recommendation to the following ASK\n" +
                "ASK: 플랫폼 이름과 버전 알수 있는 예를 3개 보여줘", prompt);
    }

}
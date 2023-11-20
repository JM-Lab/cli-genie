package kr.jm.gpt;

import kr.jm.openai.dto.Message;
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
import java.util.List;

import static kr.jm.gpt.CliGenieCommandLineTest.HELP;

// IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
@ExtendWith(MockitoExtension.class)
class CliGenieTest {
    static PrintStream previousConsole;
    static ByteArrayOutputStream newConsole;

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
    void startTestQuery() {
        CliGenie.main("Dockerfile을 사용해서 도커를 실행하는 방법");
        previousConsole.println(newConsole);
        Assertions.assertEquals(
                "도커를 실행하는 방법은 다음과 같습니다.\n" +
                        "\n" +
                        "1. Dockerfile을 작성합니다.\n" +
                        "2. Dockerfile을 빌드합니다. (docker build 명령어 사용)\n" +
                        "3. 이미지를 실행합니다. (docker run 명령어 사용)\n" +
                        "\n" +
                        "예를 들어, Dockerfile을 작성하고 빌드한 후에 다음과 같은 명령어를 사용하여 이미지를 실행할 수 있습니다.\n" +
                        "\n" +
                        "```\n" +
                        "docker run -it --rm <이미지 이름>\n" +
                        "```\n" +
                        "\n" +
                        "위 명령어에서 -it 옵션은 터미널을 사용할 수 있도록 해주고, --rm 옵션은 컨테이너가 종료되면 자동으로 삭제되도록 설정합니다. 이미지 이름은 Dockerfile에서 설정한 이름을 사용하면 됩니다.\n" +
                        "\n" +
                        "Outputs copied, please paste it: Command + V (MacOS).",
                newConsole.toString().trim());
    }

    // IMPORTANT: Please set the OPENAI_API_KEY environment variable before running tests.
    @Disabled
    @Test
    void start() {
        CliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 법");
        Assertions.assertEquals(
                "sed -i '' 's/abc/cba/g' test.txt\n\nOutputs copied, please paste it: Command + V (MacOS).",
                newConsole.toString().trim());
        previousConsole.println(newConsole);

        CliGenie.main("test.txt 파일에서 abc 라는 글자를 cba 로 바꾸는 명령 3개");
        previousConsole.println(newConsole);
        Assertions.assertEquals("sed -i '' 's/abc/cba/g' test.txt\n" +
                "\n" +
                "Outputs copied, please paste it: Command + V (MacOS).\n" +
                "1. sed -i '' 's/abc/cba/g' test.txt\n" +
                "2. perl -pi -e 's/abc/cba/g' test.txt\n" +
                "3. awk '{gsub(/abc/, \"cba\")}1' test.txt > temp && mv temp test.txt\n" +
                "\n" +
                "Outputs copied, please paste it: Command + V (MacOS).", newConsole.toString().trim());
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
                        "Outputs copied, please paste it: Command + V (MacOS).",
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
        Assertions.assertEquals(expected + "\n\nOutputs copied, please paste it: Command + V (MacOS).",
                newConsole.toString().trim());
    }

    @Disabled
    @Test
    void startAsk() {
        String expected = "java 설치 명령어: brew install java";

        CliGenie.main("java 설치 명령어");
        previousConsole.println(newConsole);
        Assertions.assertEquals(expected + "\n\nOutputs copied, please paste it: Command + V (MacOS).",
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
        List<Message> promptMessageList = new CliGenie().buildPromptMessageList("플랫폼 이름과 버전");
        Assertions.assertEquals("[Message(role=system, content=Act as CLI Assistant for Mac OS X 10.16\n" +
                "- Do Not: explanations and code blocks(```)\n" +
                "- Response: in user's language), Message(role=user, content=플랫폼 이름과 버전)]", promptMessageList.toString());
        promptMessageList = new CliGenie().buildPromptMessageList("플랫폼 이름과 버전 알수 있는 예를 3개 보여줘");
        Assertions.assertEquals("[Message(role=system, content=Act as CLI Assistant for Mac OS X 10.16\n" +
                "- Do Not: explanations and code blocks(```)\n" +
                "- Response: in user's language), Message(role=user, content=플랫폼 이름과 버전 알수 있는 예를 3개 보여줘)]", promptMessageList.toString());
    }

    @Test
    void analysisTokenTest() {
        CliGenie.main("-tc", "Dockerfile을 사용해서 도커를 실행하는 방법");
        previousConsole.println(newConsole);
        Assertions.assertEquals("Tokens  Character TOKEN IDS\n" +
                "17      28        [35, 13973, 1213, 18359, 41820, 97237, 65905, 226, 168, 119, 97, 18918, 86888, 44005, 75908, 28617, 243]\n" +
                "\n" + "\n" +
                "Outputs copied, please paste it: Command + V (MacOS).", newConsole.toString().trim());
    }

}
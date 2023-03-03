package kr.jm.gpt;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CliGenieCommandLineTest {

    @Test
    void start() {
        CliGenieCommandLine cliGenieCommandLine = new CliGenieCommandLine();

        PrintStream previousConsole = System.out;
        ByteArrayOutputStream newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));
//        gptCliHelperInit.start("-c",
//                JMResources.getURL("JMMetricConfigTest.json").getPath());
        cliGenieCommandLine.start();
        assertEquals("usage: cg [instructions in mother tongue] [-h]\n" +
                "\n" +
                "Example:\n" +
                "cg replace the letters \"abc\" with \"cba\" in the file test.txt\n" +
                "\n" +
                "Options:\n" +
                " -h,--help   print help message\n" +
                "CLI Genie: https://github.com/JM-Lab/cli-genie\n", newConsole.toString());
        System.setOut(previousConsole);
    }
}
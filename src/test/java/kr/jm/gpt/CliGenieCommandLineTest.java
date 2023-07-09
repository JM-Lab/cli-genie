package kr.jm.gpt;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CliGenieCommandLineTest {

    public static final String HELP = "usage: cg [Options] <instructions in mother tongue>\n" +
            "* Use \\ or enclose special characters in instructions.\n" +
            "\n" +
            "Example:\n" +
            "cg replace the letters \"abc\" with \"cba\" in the file test.txt\n" +
            "\n" +
            "Options:\n" +
            " -g,--general                General query to GPT\n" +
            " -h,--help                   Print help message\n" +
            " -n,--no                     Do not use copy to clipboard\n" +
            " -tc,--token-counter-cl100   Count tokens based on cl100k encoding\n" +
            "\n" +
            "To ask general questions to GPT, use 'cgg' (same as 'cg -g') in linux or mac.\n" +
            "CLI Genie: https://github.com/JM-Lab/cli-genie";

    @Test
    void buildCliOptionsPromptTest1() {
        CliGenieCommandLine cliGenieCommandLine = new CliGenieCommandLine();
        PrintStream previousConsole = System.out;
        ByteArrayOutputStream newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));
        assertNull(cliGenieCommandLine.buildCliOptionsPrompt("-h", "-n", "ldsjaf"));
        assertEquals(HELP, newConsole.toString().trim());
        System.setOut(previousConsole);
    }

    @Test
    void buildCliOptionsPromptTest2() {
        CliGenieCommandLine cliGenieCommandLine = new CliGenieCommandLine();
        PrintStream previousConsole = System.out;
        ByteArrayOutputStream newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));
        assertNull(cliGenieCommandLine.buildCliOptionsPrompt("-n"));
        assertEquals(HELP, newConsole.toString().trim());
        System.setOut(previousConsole);
    }

    @Test
    void buildCliOptionsPromptTest3() {
        CliGenieCommandLine cliGenieCommandLine = new CliGenieCommandLine();
        PrintStream previousConsole = System.out;
        ByteArrayOutputStream newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));
        assertNull(cliGenieCommandLine.buildCliOptionsPrompt());
        assertEquals("", newConsole.toString().trim());
        System.setOut(previousConsole);
    }

    @Test
    void printHelpTest() {
        CliGenieCommandLine cliGenieCommandLine = new CliGenieCommandLine();
        PrintStream previousConsole = System.out;
        ByteArrayOutputStream newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));
        cliGenieCommandLine.printHelp();
        assertEquals(HELP, newConsole.toString().trim());
        System.setOut(previousConsole);
    }

}
package kr.jm.gpt;

import kr.jm.gpt.openai.OpenAiChatCompletions;
import kr.jm.utils.JMArrays;
import kr.jm.utils.JMOptional;
import kr.jm.utils.JMResources;
import kr.jm.utils.JMString;
import kr.jm.utils.enums.OS;
import kr.jm.utils.helper.JMFile;
import kr.jm.utils.helper.JMPath;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class CliGenie {
    private final String context;
    private final GptCompletionsInterface gptCompletions;

    public CliGenie(GptCompletionsInterface gptCompletions) {
        String lineSeparator = OS.getLineSeparator();
        this.context = "Platform: " + OS.getOsName() + lineSeparator + "Version: " + OS.getOsVersion() +
                lineSeparator + "Do Not: explanations" + lineSeparator +
                "Generate a shell command or recommendation to ";
        this.gptCompletions = gptCompletions;
    }

    public String spell(String nativeLang) {
        return JMOptional.getOptional(nativeLang).map(this::buildPrompt)
                .map(this.gptCompletions::request).map(String::trim)
                .orElse("I'm sorry, I can't offer any help with that.");
    }

    String buildPrompt(String nativeLang) {
        return context + nativeLang;
    }

    public static void main(String... args) {
        CliGenieCommandLine cliGenieCommandLine = new CliGenieCommandLine();
        if (JMArrays.isNullOrEmpty(args))
            cliGenieCommandLine.printHelp();
        else
            Optional.ofNullable(cliGenieCommandLine.buildCliOptionsPrompt(args))
                    .ifPresent(CliGenie::handleOptionAndSpell);
    }

    private static void handleOptionAndSpell(CliOptionsPrompt cliOptionsPrompt) {
        if (JMString.isNullOrEmpty(cliOptionsPrompt.getPrompt()))
            return;
        CliGenie cliGenie = new CliGenie(new OpenAiChatCompletions(getOpenaiApiKey()));
        String result = cliGenie.spell(cliOptionsPrompt.getPrompt());
        System.out.println(result);
        handlOptions(result, cliOptionsPrompt.getOptions());
    }

    private static void handlOptions(String result, Set<String> options) {
        if (Objects.isNull(options) || !options.contains("no")) {
            String osName = System.getProperty("os.name").toLowerCase();
            OS.addShutdownHook(() -> copyToClipboard(osName, result));
            showCopyAndPasteInfo(osName);
        }
    }

    private static void copyToClipboard(String osName, String result) {
        try {
            if (System.getenv("SSH_CLIENT") != null || System.getProperty("java.awt.headless") != null)
                copyWithCliToClipboard(result, osName);
            else
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(result), null);
        } catch (IOException e) {
            throw new UnsupportedOperationException("Unsupported OS: " + osName);
        }
    }

    private static void showCopyAndPasteInfo(String osName) {
        if (osName.contains("linux")) {
            System.out.println(OS.getLineSeparator() +
                    "Copied GPT's response to clipboard. Paste shortcut: Ctrl + Shift + V (Linux).");
        } else if (osName.contains("mac")) {
            System.out.println(
                    OS.getLineSeparator() + "Copied GPT's response to clipboard. Paste shortcut: Command + V (MacOS).");
        } else if (osName.contains("windows")) {
            System.out.println(
                    OS.getLineSeparator() + "Copied GPT's response to clipboard. Paste shortcut: Ctrl + V (Windows).");
        }
    }

    private static void copyWithCliToClipboard(String result, String osName) throws IOException {
        if (osName.contains("linux")) {
            Runtime.getRuntime().exec("echo '" + result + "' | xclip -selection clipboard");
        } else if (osName.contains("mac")) {
            Runtime.getRuntime().exec("echo '" + result + "' | pbcopy");
        } else if (osName.contains("windows")) {
            Runtime.getRuntime().exec("cmd.exe /c echo " + result + " | clip");
        } else {
            System.out.println("Unsupported OS: " + osName);
        }
    }

    private static String getOpenaiApiKey() {
        Path openAiApiKeyFilePath = Paths.get(System.getProperty("user.home"), ".cg", "openai-api-key");
        return JMOptional.getOptional(System.getenv("OPENAI_API_KEY"))
                .map(openAiKey -> saveOpenApiKey(openAiApiKeyFilePath, openAiKey))
                .or(() -> JMResources.getStringOptionalWithFilePath(openAiApiKeyFilePath.toString()))
                .orElseThrow(() -> new RuntimeException("The OPENAI_API_KEY environment variable is not set."));
    }

    private static String saveOpenApiKey(Path openAiApiKeyFilePath, String openAiApiKey) {
        if (!JMPath.getInstance().exists(openAiApiKeyFilePath))
            OS.addShutdownHook(() -> {
                JMPath.getInstance().createDirectory(openAiApiKeyFilePath.getParent());
                JMFile.getInstance().writeString(openAiApiKey, openAiApiKeyFilePath.toFile());
            });
        return openAiApiKey;
    }

}

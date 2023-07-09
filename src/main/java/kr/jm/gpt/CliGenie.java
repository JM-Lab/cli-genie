package kr.jm.gpt;

import com.knuddels.jtokkit.api.EncodingType;
import kr.jm.gpt.token.GptTokenAnalyzer;
import kr.jm.gpt.token.TokenAnalysis;
import kr.jm.openai.OpenAiChatCompletions;
import kr.jm.openai.dto.Message;
import kr.jm.openai.dto.OpenAiChatCompletionsRequest;
import kr.jm.openai.dto.Role;
import kr.jm.openai.sse.OpenAiSseChatCompletionsPartConsumer;
import kr.jm.utils.JMArrays;
import kr.jm.utils.JMOptional;
import kr.jm.utils.JMResources;
import kr.jm.utils.enums.OS;
import kr.jm.utils.helper.JMFile;
import kr.jm.utils.helper.JMPath;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class CliGenie {
    private final String userPromptFormat;

    public CliGenie() {
        String lineSeparator = OS.getLineSeparator();
        this.userPromptFormat =
                "Platform: " + OS.getOsName() + lineSeparator + "Version: " + OS.getOsVersion() + lineSeparator +
                        "Generate a shell command or recommendation to %s" + lineSeparator +
                        "- Do Not: explanations and code blocks(```)\n- Response: in my language";
    }

    public String spell(String prmpter, Function<String, String> spellFunction) {
        return JMOptional.getOptional(prmpter).map(spellFunction).map(String::trim).orElseThrow();
    }

    String buildPromptWithCondition(String nativeLang) {
        return String.format(userPromptFormat, nativeLang);
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
        System.out.println();
        handlePostOptions(handleGptPromptOption(cliOptionsPrompt.getOptions(), cliOptionsPrompt.getPrompt()),
                cliOptionsPrompt.getOptions());
    }

    private static String handleGptPromptOption(Set<String> cliOptions, String prompt) {
        return cliOptions.contains("tc") ? handleTokenCounterOption(
                new GptTokenAnalyzer(EncodingType.CL100K_BASE).analysis(prompt))
                : handleOptionAndSpell(new CliGenie(), cliOptions, prompt, new OpenAiChatCompletions(getOpenaiApiKey()),
                new OpenAiSseChatCompletionsPartConsumer(System.out::print));
    }

    private static String handleTokenCounterOption(TokenAnalysis tokenAnalysis) {
        String tokenCounterString = String.format("%-8s%-10s%s", "Tokens", "Character", "TOKEN IDS\n") +
                String.format("%-8d%-10d%s", tokenAnalysis.getTokenCount(),
                        tokenAnalysis.getPrompt().length(), tokenAnalysis.getTokenIds().toString());
        System.out.println(tokenCounterString);
        return tokenCounterString;
    }

    private static String handleOptionAndSpell(CliGenie cliGenie, Set<String> cliOptions, String prompt,
            OpenAiChatCompletions openAiChatCompletions,
            OpenAiSseChatCompletionsPartConsumer openAiSseChatCompletionsPartConsumer) {
        return Optional.ofNullable(
                handleGeneralQuery(cliGenie, cliOptions, prompt, openAiChatCompletions,
                        openAiSseChatCompletionsPartConsumer)).orElseGet(() -> handleCliQuery(cliGenie, prompt,
                openAiChatCompletions, openAiSseChatCompletionsPartConsumer));
    }

    private static String handleGeneralQuery(CliGenie cliGenie, Set<String> cliOptions, String prompt,
            OpenAiChatCompletions openAiChatCompletions,
            OpenAiSseChatCompletionsPartConsumer openAiSseChatCompletionsPartConsumer) {
        return cliOptions.contains("g") ? cliGenie.spell(prompt,
                spell -> requestWithSse(openAiChatCompletions, openAiSseChatCompletionsPartConsumer,
                        List.of(new Message(Role.user, spell)), 1D)) : null;
    }

    private static String requestWithSse(OpenAiChatCompletions openAiChatCompletions,
            OpenAiSseChatCompletionsPartConsumer openAiSseChatCompletionsPartConsumer, List<Message> messages,
            Double temperature) {
        try {
            return openAiChatCompletions.requestWithSse(
                    new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setMaxTokens(3000)
                            .setTemperature(temperature).setStream(true).setMessages(messages),
                    openAiSseChatCompletionsPartConsumer).get().getChoices().get(0).getMessage().getContent();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static String handleCliQuery(CliGenie cliGenie, String prompt,
            OpenAiChatCompletions openAiChatCompletions,
            OpenAiSseChatCompletionsPartConsumer openAiSseChatCompletionsPartConsumer) {
        return cliGenie.spell(prompt,
                spell -> requestWithSse(openAiChatCompletions, openAiSseChatCompletionsPartConsumer,
                        List.of(new Message(Role.user, cliGenie.buildPromptWithCondition(spell))), 0D));
    }

    private static void handlePostOptions(String result, Set<String> options) {
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
                    "Outputs copied, please paste it: Ctrl + Shift + V (Linux).");
        } else if (osName.contains("mac")) {
            System.out.println(
                    OS.getLineSeparator() + "Outputs copied, please paste it: Command + V (MacOS).");
        } else if (osName.contains("windows")) {
            System.out.println(
                    OS.getLineSeparator() + "Outputs copied, please paste it: Ctrl + V (Windows).");
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

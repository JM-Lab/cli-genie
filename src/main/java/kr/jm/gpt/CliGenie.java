package kr.jm.gpt;

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
    private final String preConditionPrompt;


    public CliGenie() {
        String lineSeparator = OS.getLineSeparator();
        this.preConditionPrompt = "Platform: " + OS.getOsName() + lineSeparator + "Version: " + OS.getOsVersion() +
                lineSeparator + "Do Not: explanations" + lineSeparator +
                "Generate a shell command or recommendation to the following ASK" + lineSeparator + "ASK: ";
    }

    public String spell(String prmpter, Function<String, String> spellFunction) {
        return JMOptional.getOptional(prmpter).map(spellFunction).map(String::trim).orElseThrow();
    }

    String buildPromptWithCondition(String nativeLang) {
        return preConditionPrompt + nativeLang;
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
        String result = handleOptionAndSpell(new CliGenie(), cliOptionsPrompt, cliOptionsPrompt.getPrompt(),
                new OpenAiChatCompletions(getOpenaiApiKey()),
                new OpenAiSseChatCompletionsPartConsumer(System.out::print));
        System.out.println();
        handlePostOptions(result, cliOptionsPrompt.getOptions());
    }

    private static String handleOptionAndSpell(CliGenie cliGenie, CliOptionsPrompt cliOptionsPrompt, String prompt,
            OpenAiChatCompletions openAiChatCompletions,
            OpenAiSseChatCompletionsPartConsumer openAiSseChatCompletionsPartConsumer) {
        return Optional.ofNullable(
                handleGeneralQuery(cliGenie, cliOptionsPrompt.getOptions(), prompt, openAiChatCompletions,
                        openAiSseChatCompletionsPartConsumer)).orElseGet(() -> handleCliQuery(cliGenie, prompt,
                openAiChatCompletions, openAiSseChatCompletionsPartConsumer));
    }

    private static String handleGeneralQuery(CliGenie cliGenie, Set<String> options, String prompt,
            OpenAiChatCompletions openAiChatCompletions,
            OpenAiSseChatCompletionsPartConsumer openAiSseChatCompletionsPartConsumer) {
        return Objects.nonNull(options) && options.contains("general") ? cliGenie.spell(prompt,
                spell -> requestWithSse(openAiChatCompletions, openAiSseChatCompletionsPartConsumer,
                        List.of(new Message(Role.user, spell)))) : null;
    }

    private static String requestWithSse(OpenAiChatCompletions openAiChatCompletions,
            OpenAiSseChatCompletionsPartConsumer openAiSseChatCompletionsPartConsumer, List<Message> messages) {
        try {
            return openAiChatCompletions.requestWithSse(
                    new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setMaxTokens(3000)
                            .setTemperature(0D).setStream(true).setMessages(messages),
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
                        List.of(new Message(Role.user, cliGenie.buildPromptWithCondition(spell)),
                                new Message(Role.system,
                                        "- don't explain.\n- don't use backticks(```).\n- in the language as the ASK."))));
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
                    "Paste: Ctrl + Shift + V (Linux).");
        } else if (osName.contains("mac")) {
            System.out.println(
                    OS.getLineSeparator() + "Paste: Command + V (MacOS).");
        } else if (osName.contains("windows")) {
            System.out.println(
                    OS.getLineSeparator() + "Paste: Ctrl + V (Windows).");
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

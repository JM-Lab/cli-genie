package kr.jm.gpt;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.EncodingType;
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
    private final Message systemMessage;
    private final String userPromptFormat;

    public CliGenie() {
        String lineSeparator = OS.getLineSeparator();
        this.systemMessage =
                new Message(Role.system,
                        "Act as CLI Assistant for " + OS.getOsName() + " " + OS.getOsVersion() + lineSeparator +
                                "- Do Not: explanations and code blocks(```)\n- Response: in user's language");
        this.userPromptFormat = "Generate a shell command or recommendation to %s";
    }

    public String spell(String prmpter, Function<String, String> spellFunction) {
        return JMOptional.getOptional(prmpter).map(spellFunction).map(String::trim).orElseThrow();
    }

    List<Message> buildPromptMessageList(String userPrompt) {
        return List.of(systemMessage, new Message(Role.user, userPrompt));
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
        return cliOptions.contains("tc") ?
                handleTokenCounterOption(Encodings.newLazyEncodingRegistry().getEncoding(EncodingType.CL100K_BASE)
                        .encodeOrdinary(prompt), prompt.length())
                : handleOptionAndSpell(new CliGenie(), cliOptions, prompt, new OpenAiChatCompletions(getOpenaiApiKey()),
                new OpenAiSseChatCompletionsPartConsumer(System.out::print));
    }

    private static String handleTokenCounterOption(List<Integer> tokenIds, int characterLength) {
        String tokenCounterString = String.format("%-8s%-10s%s", "Tokens", "Character", "TOKEN IDS\n") +
                String.format("%-8d%-10d%s", tokenIds.size(), characterLength, tokenIds);
        System.out.println(tokenCounterString);
        return tokenCounterString;
    }

    private static String handleOptionAndSpell(CliGenie cliGenie, Set<String> cliOptions, String prompt,
            OpenAiChatCompletions openAiChatCompletions,
            OpenAiSseChatCompletionsPartConsumer openAiSseChatCompletionsPartConsumer) {

        return cliOptions.contains("g") ? cliGenie.spell(prompt,
                spell -> requestWithSse(openAiChatCompletions, openAiSseChatCompletionsPartConsumer,
                        cliGenie.buildPromptMessageList(spell), 1D)) : cliGenie.spell(prompt,
                spell -> requestWithSse(openAiChatCompletions, openAiSseChatCompletionsPartConsumer,
                        cliGenie.buildPromptMessageList(String.format(cliGenie.userPromptFormat, spell)), 0D));
    }

    private static String requestWithSse(OpenAiChatCompletions openAiChatCompletions,
            OpenAiSseChatCompletionsPartConsumer openAiSseChatCompletionsPartConsumer, List<Message> messages,
            Double temperature) {
        try {
            return openAiChatCompletions.requestWithSse(
                    new OpenAiChatCompletionsRequest().setModel("gpt-3.5-turbo").setMaxTokens(3000)
                            .setTemperature(temperature).setStream(true).setMessages(messages),
                    () -> openAiSseChatCompletionsPartConsumer).get().getChoices().get(0).getMessage().getContent();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handlePostOptions(String result, Set<String> options) {
        if (Objects.isNull(options) || !options.contains("no")) {
            String osName = System.getProperty("os.name").toLowerCase();
            OS.addShutdownHook(() -> copyToClipboard(osName, result));
            Optional.ofNullable(getCopyAndPasteInfo(osName)).ifPresent(copyAndPasteInfo ->
                    System.out.println(OS.getLineSeparator() + OS.getLineSeparator() + copyAndPasteInfo));
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

    private static String getCopyAndPasteInfo(String osName) {
        if (osName.contains("linux"))
            return "Outputs copied, please paste it: Ctrl + Shift + V (Linux).";
        else if (osName.contains("mac"))
            return "Outputs copied, please paste it: Command + V (MacOS).";
        else if (osName.contains("windows"))
            return "Outputs copied, please paste it: Ctrl + V (Windows).";
        else return null;
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

package kr.jm.gpt;

import kr.jm.gpt.openai.OpenAiChatCompletions;
import kr.jm.utils.JMOptional;
import kr.jm.utils.JMResources;
import kr.jm.utils.enums.OS;
import kr.jm.utils.helper.JMFile;
import kr.jm.utils.helper.JMPath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CliGenie {
    private final String context;
    private final GptCompletionsInterface gptCompletions;

    public CliGenie(GptCompletionsInterface gptCompletions) {
        String lineSeparator = OS.getLineSeparator();
        this.context = "Platform: " + OS.getOsName() + lineSeparator + "Version: " + OS.getOsVersion() +
                lineSeparator + "Do Not: explanations" + lineSeparator + "Generate a shell command or recommendation to ";
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
        String openaiApiKey = getOpenaiApiKey();
        Optional.ofNullable(new CliGenieCommandLine().start(args))
                .map(new CliGenie(new OpenAiChatCompletions(openaiApiKey))::spell).ifPresent(System.out::println);
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

package kr.jm.gpt;


import kr.jm.utils.JMOptional;
import kr.jm.utils.JMStream;
import kr.jm.utils.JMString;
import org.apache.commons.cli.*;

import java.util.stream.Collectors;

public class CliGenieCommandLine {

    private final Options options;

    public CliGenieCommandLine() {
        this.options = buildCLIOptions();
    }

    public CliOptionsPrompt buildCliOptionsPrompt(String... args) {
        return JMOptional.getOptional(args).map(this::parseCLI)
                .map(this::buildCommandLine).orElse(null);
    }

    private CommandLine parseCLI(String... args) {
        try {
            CommandLine commandLine = new DefaultParser().parse(this.options, args, true);
            return commandLine.hasOption("help") || commandLine.getArgList().isEmpty() ? printHelp() : commandLine;
        } catch (ParseException e) {
            printHelp();
            throw new RuntimeException(e);
        }
    }

    public CommandLine printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        String syntax =
                "cg [Options] <instructions in mother tongue>\n* Use \\ or enclose special characters in instructions.";
        String usageHeader = "\nExample:\ncg replace the letters \"abc\" with \"cba\" in the file test" +
                ".txt\n\nOptions:";
        String usageFooter = "\nTo ask general questions to GPT, use 'cgg' (same as 'cg -g') in linux or mac.\nCLI " +
                "Genie:" +
                " https://github.com/JM-Lab/cli-genie";
        formatter.printHelp(120, syntax, usageHeader, this.options, usageFooter, false);
        return null;
    }

    private Options buildCLIOptions() {
        Options options = new Options();
        options.addOption("g", "general", false, "General query to GPT");
        options.addOption("h", "help", false, "Print help message");
        options.addOption("n", "no", false, "Do not use copy to clipboard");
        options.addOption("tc", "token-counter-cl100", false, "Count tokens based on cl100k encoding");
        return options;
    }

    private CliOptionsPrompt buildCommandLine(CommandLine commandLine) {
        CliOptionsPrompt cliOptionsPrompt = new CliOptionsPrompt();
        cliOptionsPrompt.setOptions(JMStream.buildStream(commandLine.getOptions()).map(Option::getOpt)
                .collect(Collectors.toSet()));
        return cliOptionsPrompt.setPrompt(JMString.joiningWithSpace(commandLine.getArgList()));
    }

}

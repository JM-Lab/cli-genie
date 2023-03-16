package kr.jm.gpt;


import kr.jm.utils.JMOptional;
import kr.jm.utils.JMString;
import org.apache.commons.cli.*;

import java.util.HashSet;

public class CliGenieCommandLine {

    private final Options options;

    public CliGenieCommandLine() {
        this.options = buildCLIOptions();
    }

    public CliOptionsPrompt buildCliOptionsPrompt(String... args) {
        return JMOptional.getOptional(args).map(this::parseCLI)
                .map(this::buildCommandLine).orElseGet(CliOptionsPrompt::new);
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
        String syntax = "cg [Options] <instructions in mother tongue>";
        String usageHeader = "\nExample:\ncg replace the letters \"abc\" with \"cba\" in the file test" +
                ".txt\n\nOptions:";
        String usageFooter = "CLI Genie: https://github.com/JM-Lab/cli-genie";
        formatter.printHelp(120, syntax, usageHeader, this.options, usageFooter, false);
        return null;
    }

    private Options buildCLIOptions() {
        Options options = new Options();
        options.addOption("n", "no", false, "Do not use copy to clipboard");
        options.addOption("h", "help", false, "Print help message");
//        options.addOption("c", "config", true, "a file path of config, default: ~/.cg");
        return options;
    }

    private CliOptionsPrompt buildCommandLine(CommandLine commandLine) {
        CliOptionsPrompt cliOptionsPrompt = new CliOptionsPrompt();
        if (commandLine.hasOption("no"))
            cliOptionsPrompt.setOptions(new HashSet<>()).getOptions().add("no");
        return cliOptionsPrompt.setPrompt(JMString.joiningWithSpace(commandLine.getArgList()));
    }

}

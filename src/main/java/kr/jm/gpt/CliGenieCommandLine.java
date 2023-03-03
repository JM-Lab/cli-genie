package kr.jm.gpt;


import kr.jm.utils.JMOptional;
import kr.jm.utils.JMString;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class CliGenieCommandLine {

    private final Options options;

    public CliGenieCommandLine() {
        this.options = buildCLIOptions();
    }

    public String start(String... args) {
        try {
            return JMOptional.getOptional(args).map(a -> parseCLI(this.options, args))
                    .map(this::applyCommandLine).orElseThrow();
        } catch (Exception e) {
            printHelp(this.options);
            return null;
        }
    }

    private CommandLine parseCLI(Options options, String... args) {
        try {
            return initArgs(options, new DefaultParser().parse(options, args, true));
        } catch (Exception e) {
            return printHelp(options);
        }
    }

    private CommandLine initArgs(Options options, CommandLine commandLine) {
        return commandLine.hasOption("help") ? printHelp(options) : commandLine;
    }

    private CommandLine printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        String syntax = "cg [instructions in mother tongue]";
        String usageHeader = "\nExample:\ncg replace the letters \"abc\" with \"cba\" in the file test" +
                ".txt\n\nOptions:";
        String usageFooter = "CLI Genie: https://github.com/JM-Lab/cli-genie";
        formatter.printHelp(120, syntax, usageHeader, options, usageFooter, true);
        return null;
    }

    private Options buildCLIOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "print help message");
//        options.addOption("c", "config", true, "a file path of config, default: ~/.cg");
        return options;
    }

    private String applyCommandLine(CommandLine commandLine) {
        // option 이 있으면 처리해서 return을 다르게 주어야 함
        return JMString.joiningWithSpace(commandLine.getArgList());
    }

}

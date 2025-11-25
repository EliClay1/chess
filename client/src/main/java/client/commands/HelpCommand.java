package client.commands;

import client.UserState;
import client.results.*;

import java.util.List;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class HelpCommand implements CommandInterface{

    private final int argumentCount = 0;

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public List<String> getAliases() {
        return List.of("h");
    }

    @Override
    public String getUsage() {
        return "Display this help message: \"h\", \"help\"\n";
    }

    @Override
    public boolean requiresLogin() {
        return false;
    }

    @Override
    public ValidationResult validate(String[] args, UserState userState) {
        if (args.length == argumentCount) {
            return new ValidationResult(true, "").ok();
        }
        return new ValidationResult(false, "Help doesn't take any arguments.").error();
    }

    @Override
    public CommandResult execute(String[] args, UserState userState, CommandRegistry registery) {
        System.out.printf("%sOptions:\n", SET_TEXT_COLOR_BLUE);
        for (CommandInterface command : registery.getAllCommands()) {
            if (!userState.isLoggedIn() && !command.requiresLogin()) {
                System.out.print(" - " + command.getUsage());
            } else if (userState.isLoggedIn() && command.requiresLogin()) {
                System.out.print(" - " + command.getUsage());
            }
        }
        return new CommandResult(true, "");
    }
}

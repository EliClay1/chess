package client.commands;

import client.UserState;
import client.results.CommandResult;
import client.results.ValidationResult;

import java.util.List;

public class QuitCommand implements CommandInterface{
    @Override
    public String getName() {
        return "quit";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public String getUsage() {
        return "Quit the game: \"quit\"\n";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 0;
    }

    @Override
    public boolean requiresLogin() {
        return false;
    }

    @Override
    public ValidationResult validate(String[] args, UserState userState) {
        if (args.length == 0) {
            return new ValidationResult(true, "").ok();
        }
        return new ValidationResult(false, "Quit doesn't take any arguments.").error();
    }

    @Override
    public CommandResult execute(String[] args, UserState userState, CommandRegistry registery) {
        return new CommandResult(true, "");
    }
}

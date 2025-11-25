package client.commands;

import client.UserState;
import client.results.CommandResult;
import client.results.ValidationResult;

import java.util.List;

public class BaseCommand implements CommandInterface {

    public final int argumentCount = 1;

    @Override
    public String getName() {
        return "";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean requiresLogin() {
        return false;
    }

    @Override
    public ValidationResult validate(String[] args, UserState userState) {
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, String.format("Incorrect amount of arguments, expected %d.", argumentCount));
    }

    @Override
    public CommandResult execute(String[] args, UserState userState, CommandRegistry registery) {
        return null;
    }
}

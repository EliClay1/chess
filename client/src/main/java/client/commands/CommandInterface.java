package client.commands;

import client.UserState;
import client.results.CommandResult;
import client.results.ValidationResult;

import java.util.List;

public interface CommandInterface {
    String getName();
    List<String> getAliases();
    String getUsage();
    boolean requiresLogin();
    ValidationResult validate(String[] args, UserState userState);
    CommandResult execute(String[] args, UserState userState, CommandRegistry registery) throws Exception;
}

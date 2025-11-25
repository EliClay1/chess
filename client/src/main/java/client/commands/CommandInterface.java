package client.commands;

import client.UserStateData;
import client.results.CommandResult;
import client.results.ValidationResult;

import java.util.List;

public interface CommandInterface {
    String getName();
    List<String> getAliases();
    String getUsage();
    boolean requiresLogin();
    ValidationResult validate(String[] args, UserStateData userStateData);
    CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery) throws Exception;
}

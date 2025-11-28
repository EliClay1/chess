package client.commands;

import client.ClientState;
import client.UserStateData;
import client.results.CommandResult;
import client.results.ValidationResult;

import java.util.Collection;
import java.util.List;

public interface CommandInterface {
    String getName();
    List<String> getAliases();
    String getUsage();
    Collection<ClientState> allowedStates();
    ValidationResult validate(String[] args, UserStateData userStateData);
    CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery) throws Exception;
}

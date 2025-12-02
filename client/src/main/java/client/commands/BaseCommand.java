package client.commands;

import client.ClientState;
import client.UserStateData;
import client.results.CommandResult;
import client.results.ValidationResult;
import websocket.commands.UserGameCommand;

import java.util.ArrayList;
import java.util.Collection;
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
    public Collection<ClientState> allowedStates() {
        return List.of();
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, String.format("Incorrect amount of arguments, expected %d.", argumentCount));
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery,
                                 UserGameCommand.CommandType commandType) throws Exception {
        return null;
    }
}

package client.commands;

import client.ClientState;
import client.UserStateData;
import client.results.CommandResult;
import client.results.ValidationResult;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.List;

public class QuitCommand implements CommandInterface{
    private final int argumentCount = 0;

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
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.LOGGED_OUT);
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        if (args.length == argumentCount) {
            return new ValidationResult(true, "").ok();
        }
        return new ValidationResult(false, "Quit doesn't take any arguments.").error();
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery,
                                 UserGameCommand.CommandType commandType) {
        return new CommandResult(true, "");
    }
}

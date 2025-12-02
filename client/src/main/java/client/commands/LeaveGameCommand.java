package client.commands;

import client.ClientState;
import client.UserStateData;
import client.results.CommandResult;
import client.results.ValidationResult;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.List;

public class LeaveGameCommand extends BaseLeaveCommand {

    private final int argumentCount = 0;

    public LeaveGameCommand() {
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getUsage() {
        return "Leave the game: \"leave\"\n";
    }

    @Override
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.PLAYING_GAME, ClientState.OBSERVING_GAME);
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, String.format("Incorrect amount of arguments, expected %d.", argumentCount));
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery,
                                 UserGameCommand.CommandType commandType) {
        return executeCommand(userState, UserGameCommand.CommandType.LEAVE);
    }
}

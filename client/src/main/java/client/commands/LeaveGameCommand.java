package client.commands;

import client.ClientState;
import client.UserStateData;
import client.results.CommandResult;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.List;

public class LeaveGameCommand extends BaseLeaveCommand {

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
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery,
                                 UserGameCommand.CommandType commandType) {
        return executeCommand(userState, UserGameCommand.CommandType.LEAVE);
    }
}

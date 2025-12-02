package client.commands;

import client.ClientState;
import client.UserStateData;
import client.results.CommandResult;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.List;

public class ResignCommand extends BaseLeaveCommand {

    public ResignCommand() {
    }

    @Override
    public String getName() {
        return "resign";
    }

    @Override
    public List<String> getAliases() {
        return List.of("r");
    }

    @Override
    public String getUsage() {
        return "Resign & forfeit the game: \"resign\", \"r\"\n";
    }

    @Override
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.PLAYING_GAME);
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery,
                                 UserGameCommand.CommandType commandType) {
        return executeCommand(userState, commandType);
    }
}

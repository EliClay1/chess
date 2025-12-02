package client.commands;

import client.ClientState;
import client.ServerFacade;
import client.UserStateData;
import client.commands.command_implementation.BaseCommand;
import client.commands.command_implementation.CommandRegistry;
import client.results.CommandResult;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.List;

public class LeaveGameCommand extends BaseCommand {

    private final ServerFacade serverFacade = new ServerFacade();
    private WebsocketFacade websocketFacade;
    private final int argumentCount = 0;
    private UserStateData userStateData;

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
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery) {
        this.userStateData = userState;
        try {
            websocketFacade = userStateData.getWebsocketFacade();
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, userStateData.getAuthToken(),
                    userStateData.getActiveGameId(), null);
            websocketFacade.sendMessage(new Gson().toJson(command));

            userStateData.setClientState(ClientState.LOGGED_IN);
            userStateData.setActiveTeamColor(null);
            userStateData.setActiveGameId(0);
            return new CommandResult(true, "");
        } catch (Exception e) {
            if (e instanceof NumberFormatException) {
                return new CommandResult(false, "Invalid GameID.");
            } else if (e instanceof IllegalStateException) {
                return new CommandResult(false, "Error: " + e.getMessage());
            }
            else {
                // TODO - if the connection is closed, it should quit the user out of the game. Basically run leave
                return new CommandResult(false, "Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        BaseCommand.notifyMethod(serverMessage);
    }
}

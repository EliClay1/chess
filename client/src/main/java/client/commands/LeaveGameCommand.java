package client.commands;

import client.ClientState;
import client.ServerFacade;
import client.UserStateData;
import client.results.CommandResult;
import client.results.ValidationResult;
import client.websocket.NotificationHandler;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class LeaveGameCommand implements CommandInterface, NotificationHandler {

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
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public String getUsage() {
        return "Leave the game: \"leave\"\n";
    }

    @Override
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.LOGGED_IN, ClientState.PLAYING_GAME);
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, String.format("Incorrect amount of arguments, expected %d.", argumentCount));
    }

    // TODO - When the user leaves, their Websocket Connection is actually closed.

    @Override
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery) {
        userStateData = userState;

        try {
            websocketFacade = userStateData.getWebsocketFacade();
            UserGameCommand leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, userStateData.getAuthToken(),
                    userStateData.getActiveGameId(), "");
            websocketFacade.sendMessage(new Gson().toJson(leaveCommand));
            // reset character state.
            userStateData.setClientState(ClientState.LOGGED_IN);
            userStateData.setActiveTeamColor(null);
            userStateData.setActiveGameId(0);

            return new CommandResult(true, "");
        } catch (Exception e) {
            if (e instanceof NumberFormatException) {
                return new CommandResult(false, "Invalid GameID.");
            } else if (e instanceof IllegalStateException) {
                // TODO - figure out some kind of error handling for when the connection gets closed.
                // could have a kill sequence that will drop all players from the game to prevent continuity issues.
                return new CommandResult(false, "Error: " + e.getMessage());
            }
            else {
                return new CommandResult(false, "Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            String message = serverMessage.getMessage();
            System.out.printf("\u001b[38;5;%dm%s%s\n", 4, message, RESET_TEXT_COLOR);
        } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            String message = serverMessage.getMessage();
            System.out.printf("\u001b[38;5;%dm%s%s\n", 1, message, RESET_TEXT_COLOR);
        }
    }
}

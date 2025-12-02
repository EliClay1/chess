package client.commands;

import client.ClientState;
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

public class ResignCommand implements CommandInterface, NotificationHandler {

    private WebsocketFacade websocketFacade;
    private final int argumentCount = 0;
    private UserStateData userStateData;

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
            UserGameCommand leaveCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, userStateData.getAuthToken(),
                    userStateData.getActiveGameId(), null);
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

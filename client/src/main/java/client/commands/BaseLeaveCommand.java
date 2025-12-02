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

public class BaseLeaveCommand implements CommandInterface, NotificationHandler {

    protected WebsocketFacade websocketFacade;
    protected final int argumentCount = 0;
    protected UserStateData userStateData;

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
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery,
                                 UserGameCommand.CommandType commandType) {
        return null;
    }

    protected CommandResult executeCommand(UserStateData userState, UserGameCommand.CommandType commandType) {
        userStateData = userState;
        try {
            websocketFacade = userStateData.getWebsocketFacade();
            UserGameCommand command = new UserGameCommand(commandType, userStateData.getAuthToken(),
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

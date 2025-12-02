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
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery) {
        userStateData = userState;

        try {
            websocketFacade = userStateData.getWebsocketFacade();
            UserGameCommand leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, userStateData.getAuthToken(),
                    userStateData.getActiveGameId(), null);
            websocketFacade.sendMessage(new Gson().toJson(leaveCommand));
            // reset character state.
            userStateData.setClientState(ClientState.LOGGED_IN);
            userStateData.setActiveTeamColor(null);
            userStateData.setActiveGameId(0);

            return new CommandResult(true, "");
        } catch (Exception e) {
            return ResignCommand.getErrorResult(e);
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        ResignCommand.notificationCode(serverMessage);
    }
}

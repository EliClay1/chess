package client.commands;

import chess.ChessGame;
import chess.ChessMove;
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

public class MakeMoveCommand implements CommandInterface, NotificationHandler {

    private final ServerFacade serverFacade = new ServerFacade();
    private WebsocketFacade websocketFacade;
    private final int argumentCount = 1;
    private UserStateData userStateData;

    public MakeMoveCommand() {
    }

    @Override
    public String getName() {
        return "move";
    }

    @Override
    public List<String> getAliases() {
        return List.of("m");
    }

    @Override
    public String getUsage() {
        return "Make a move: \"m\", \"move\" <move1,move2>\n";
    }

    @Override
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.PLAYING_GAME);
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        // argument length check
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, String.format("Incorrect amount of arguments, expected %d.", argumentCount));
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery) {
        userStateData = userState;
        String[] moveParts = args[0].split(",");
        ChessMove

        // TODO - Move needs to be converted into an actual chess move BEFORE sending to the websocket. ie. here.


//        System.out.println("DEBUG: About to get websocketFacade");
        try {
            websocketFacade = userStateData.getWebsocketFacade();
//            System.out.println("DEBUG: Got websocketFacade: " + (websocketFacade != null));
            websocketFacade.setNotificationHandler(this);
//            System.out.println("DEBUG: Set handler to MakeMoveCommand");
            UserGameCommand moveCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, userStateData.getAuthToken(),
                    userStateData.getActiveGameId(), move);
            websocketFacade.sendMessage(new Gson().toJson(moveCommand));

            return new CommandResult(true, "");
        } catch (Exception e) {
            if (e instanceof NumberFormatException) {
                return new CommandResult(false, "Invalid GameID.");
            } else {
                return new CommandResult(false, "Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            ChessGame chessGame = serverMessage.getGame();
            serverFacade.printBoard(userStateData.getActiveTeamColor(), chessGame);
        } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            String message = serverMessage.getMessage();
            System.out.printf("\u001b[38;5;%dm%s%s\n", 4, message, RESET_TEXT_COLOR);
            System.out.printf("\u001b[38;5;%dm%s%s", 6, "[Playing] >>> ", RESET_TEXT_COLOR);
        } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            String message = serverMessage.getMessage();
            System.out.printf("\u001b[38;5;%dm%s%s\n", 1, message, RESET_TEXT_COLOR);
            System.out.printf("\u001b[38;5;%dm%s%s", 6, "[Playing] >>> ", RESET_TEXT_COLOR);
        }
    }
}

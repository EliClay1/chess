package websocket.commands;

public class MoveCommand extends UserGameCommand{
    public MoveCommand(CommandType commandType, String authToken, Integer gameID, String move) {
        super(commandType, authToken, gameID);
    }

    public void calculateMove(String move) {

    }
}

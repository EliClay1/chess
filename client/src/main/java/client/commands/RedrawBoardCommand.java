package client.commands;

import client.ClientState;
import client.ServerFacade;
import client.UserStateData;
import client.websocket.WebsocketFacade;

import java.util.Collection;
import java.util.List;

public class RedrawBoardCommand extends PrintCommand {

    public final int argumentCount = 0;
    private final ServerFacade serverFacade = new ServerFacade();
    private UserStateData userStateData;
    private WebsocketFacade websocketFacade;

    @Override
    public String getName() {
        return "redraw";
    }

    @Override
    public List<String> getAliases() {
        return List.of("rd");
    }

    @Override
    public String getUsage() {
        return "Redraw the game board: \"rd\", \"redraw\"\n";
    }

    @Override
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.PLAYING_GAME, ClientState.OBSERVING_GAME);
    }
}

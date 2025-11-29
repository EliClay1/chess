package client;

import client.websocket.WebsocketFacade;

import java.util.List;
import java.util.Map;

public class UserStateData {

    private static String host;
    private static int port;
    private static String authToken;
    private static String username;
    private static ClientState clientState;
    private static List<Map<String, String>> activeGames;
    private static int activeGameId;
    private static String activeTeamColor;
    private static WebsocketFacade websocketFacade;

    public UserStateData(String host, int port, String authToken, String username, ClientState clientState,
                         List<Map<String, String>> activeGames, int activeGameId, WebsocketFacade wsFacade, String teamColor) {
        setHost(host);
        setPort(port);
        setAuthToken(authToken);
        setUsername(username);
        setClientState(clientState);
        setActiveGames(activeGames);
        setActiveGameId(activeGameId);
        setWebsocketFacade(wsFacade);
        setActiveTeamColor(teamColor);
}

    public List<Map<String, String>> getActiveGames() {
        return activeGames;
    }

    public void setActiveGames(List<Map<String, String>> activeGames) {
        UserStateData.activeGames = activeGames;
    }

    public ClientState clientState() {
        return clientState;
    }

    public void setClientState(ClientState clientState) {
        UserStateData.clientState = clientState;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        UserStateData.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        UserStateData.authToken = authToken;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        UserStateData.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        UserStateData.host = host;
    }

    public int getActiveGameId() {
        return activeGameId;
    }

    public void setActiveGameId(int id) {
        UserStateData.activeGameId = id;
    }

    public void setWebsocketFacade(WebsocketFacade websocketFacade) {
        UserStateData.websocketFacade = websocketFacade;
    }

    public WebsocketFacade getWebsocketFacade() {
        return websocketFacade;
    }

    public void setActiveTeamColor(String teamColor) {
        UserStateData.activeTeamColor = teamColor;
    }

    public String getActiveTeamColor() {
        return activeTeamColor;
    }
}

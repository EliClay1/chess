package client;

import dataaccess.MySQLDataAccess;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.AlreadyTakenException;
import exceptions.InvalidException;
import exceptions.UnauthorizedException;
import org.junit.jupiter.api.*;
import server.Server;

public class ServerFacadeTests {

    private static Server server;
    private MySQLDataAccess db;
    private ServerFacade serverFacade;
    private static int actualPort;


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        actualPort = port;
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        db = new MySQLDataAccess();
        db.clear();
        serverFacade = new ServerFacade();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerPass() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        assertEquals(200, serverFacade.status);
    }

    @Test
    public void registerBadPassword() {
        assertThrows(InvalidException.class, () -> serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "#password1", "bob@gmail.com"));
    }

    @Test
    public void registerUserAlreadyExists() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        assertThrows(AlreadyTakenException.class, () -> serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com"));
    }

    @Test
    public void loginPass() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        assertEquals(200, serverFacade.status);
    }

    @Test
    public void loginIncorrectPassword() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        assertThrows(UnauthorizedException.class, () -> serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password!"));
    }

    @Test
    public void loginIllegalUsername() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        assertThrows(InvalidException.class, () -> serverFacade.loginUser("localhost", actualPort,
                "/session", "#bob", "password1"));
    }

    @Test
    public void logoutPass() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        serverFacade.logoutUser("localhost", actualPort, "/session", authMap.get("authToken"));
        assertEquals(200, serverFacade.status);
    }

    @Test
    public void logoutInvalidAuth() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        assertThrows(Exception.class, () -> serverFacade.logoutUser("localhost", actualPort, "/session", "0"));
    }

    @Test
    public void listPass() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        serverFacade.listGames("localhost", actualPort, "/game", authMap.get("authToken"));
        assertEquals(200, serverFacade.status);
    }

    @Test
    public void listInvalidAuth() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        assertThrows(Exception.class, () -> serverFacade.listGames("localhost", actualPort, "/game", "0"));
    }

    @Test
    public void createGamePass() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Bobs_Game");
        assertEquals(200, serverFacade.status);
    }

    @Test
    public void createGameInvalidAuth() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        assertThrows(Exception.class, () -> serverFacade.createGame("localhost", actualPort,
                "/game", "0", "Bobs_Game"));
    }

    @Test
    public void createGameInvalidGameName() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        assertThrows(InvalidException.class, () -> serverFacade.createGame("localhost", actualPort, "/game",
                authMap.get("authToken"), "#Bobs_Game"));
    }

    @Test
    public void joinGamePass() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Bobs_Game");
        serverFacade.joinGame("localhost", actualPort, "/game", authMap.get("authToken"), "1", "white");
        assertEquals(200, serverFacade.status);
    }

    @Test
    public void joinGameInvalidColor() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Bobs_Game");
        assertThrows(InvalidException.class, () -> serverFacade.joinGame("localhost", actualPort, "/game",
                authMap.get("authToken"), "1", "#white"));
        assertThrows(InvalidException.class, () -> serverFacade.joinGame("localhost", actualPort, "/game",
                authMap.get("authToken"), "1", "blue"));
        assertThrows(InvalidException.class, () -> serverFacade.joinGame("localhost", actualPort, "/game",
                authMap.get("authToken"), "1", ""));
    }

    @Test
    public void joinGameInvalidGameID() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Bobs_Game");
        assertThrows(Exception.class, () -> serverFacade.joinGame("localhost", actualPort, "/game",
                authMap.get("authToken"), "p", "white"));
    }

    @Test
    public void joinGameNonexistent() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Bobs_Game");
        assertThrows(Exception.class, () -> serverFacade.joinGame("localhost", actualPort, "/game",
                authMap.get("authToken"), "2", "white"));
    }

    @Test
    public void observeGamePass() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Bobs_Game");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Joes_Game");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Steves_Game");
        var gameList = serverFacade.listGames("localhost", actualPort, "/game", authMap.get("authToken"));
        serverFacade.observeGame("1", gameList);
        assertEquals(200, serverFacade.status);
    }

    @Test
    public void observeGameNoList() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Bobs_Game");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Joes_Game");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Steves_Game");
        assertThrows(InvalidException.class, () -> serverFacade.observeGame("1", null));
    }

    @Test
    public void observeGameInvalidGameID() throws Exception {
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Bobs_Game");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Joes_Game");
        serverFacade.createGame("localhost", actualPort, "/game", authMap.get("authToken"), "Steves_Game");
        var gameList = serverFacade.listGames("localhost", actualPort, "/game", authMap.get("authToken"));
        assertThrows(Exception.class, () -> serverFacade.observeGame("p", gameList));
    }
}

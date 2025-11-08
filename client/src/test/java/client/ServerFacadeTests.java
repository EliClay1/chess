package client;

import dataaccess.MySQLDataAccess;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.InvalidException;
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
        serverFacade.registerUser("localhost", actualPort,
                "/user", "bob", "password", "bob@gmail.com");
        assertEquals(403, serverFacade.status);
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
        serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password1");
        assertEquals(401, serverFacade.status);
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
        serverFacade.logoutUser("localhost", actualPort, "/session", "0");
        assertEquals(401, serverFacade.status);
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
        var authMap = serverFacade.loginUser("localhost", actualPort,
                "/session", "bob", "password");
        serverFacade.listGames("localhost", actualPort, "/game", "0");
        assertEquals(401, serverFacade.status);
    }

}

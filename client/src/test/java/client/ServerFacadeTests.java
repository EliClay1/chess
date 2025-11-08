package client;

import com.mysql.cj.x.protobuf.MysqlxSql;
import com.sun.tools.javac.Main;
import dataaccess.MySQLDataAccess;
import org.junit.jupiter.api.*;
import server.Server;

public class ServerFacadeTests {

    private static Server server;
    private MySQLDataAccess db;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        db = new MySQLDataAccess();
        db.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerPass() throws Exception {
        Main.main(new String[]{"r bob password email@gmail.com"});

    }

}

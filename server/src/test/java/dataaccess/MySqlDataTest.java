package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


//import chess.ChessGame;
//import com.google.gson.Gson;
//import exception.ResponseException;
//import model.AllGamesData;
//import model.AuthData;
//import model.GameData;
//import model.UserData;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//
//import java.sql.*;
//
//import static java.sql.Statement.RETURN_GENERATED_KEYS;
//import static java.sql.Types.NULL;
public class MySqlDataTest {
    private static final TestUser TEST_USER = new TestUser("ExistingUser", "existingUserPassword", "eu@mail.com");

    private static TestServerFacade serverFacade;

    private static DataAccess dataAccess;

    private static Class<?> databaseManagerClass;


    @BeforeAll
    public static void startServer() throws ResponseException {

        dataAccess = new MySqlDataAccess();

    }

    @BeforeEach
    public void setUp() throws ResponseException {
        dataAccess.clear();
    }





    /*public MySqlDataAccess() throws ResponseException {
        configureDatabase();
    }*/
    @Test
    @DisplayName("Clear Test")
    @Order(1)
    public void clearTest() throws ResponseException {
        dataAccess.clear();
        Assertions.assertEquals(new ArrayList<GameData>(), dataAccess.listGames("auth"));
    }
    @Test
    @DisplayName("create User Test")
    @Order(2)
    public void createUserTest() throws ResponseException {
        Assertions.assertDoesNotThrow(() -> dataAccess.createUser(new UserData("name","password","email")));
    }
    @Test
    @DisplayName("invalid create User Test")
    @Order(3)
    public void invalidCreateUserTest() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> dataAccess.createUser(new UserData(null,"password","email")));
    }
    @Test
    @DisplayName("get User Test")
    @Order(4)
    public void getUserTest() throws ResponseException {
        dataAccess.createUser(new UserData("newUser","password","email"));
        Assertions.assertDoesNotThrow(() -> dataAccess.getUser("newUser"));

    }

    @Test
    @DisplayName("invalid get User Test")
    @Order(5)
    public void invalidUserGetUserTest() throws ResponseException {
        Assertions.assertNull(dataAccess.getUser("invalidName"));
    }

    @Test
    @DisplayName("create Game Test")
    @Order(6)
    public void createGameTest() throws ResponseException {
        Assertions.assertDoesNotThrow(() -> dataAccess.createGame("user","game"));
    }
    @Test
    @DisplayName("invalid create Game Test")
    @Order(7)
    public void invalidCreateGameTest() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> dataAccess.createGame(null,null));
    }
    @Test
    @DisplayName("get Game Test")
    @Order(8)
    public void getGameTest() throws ResponseException {
        int id = dataAccess.createGame("newUser","gameName");
        Assertions.assertDoesNotThrow(() -> dataAccess.getGame(id));
    }

    @Test
    @DisplayName("invalid game get Game Test")
    @Order(9)
    public void invalidGameGetGameTest() throws ResponseException {
        Assertions.assertNull(dataAccess.getGame(-1));
    }

    @Test
    @DisplayName("list Games Test")
    @Order(10)
    public void ListGamesTest() throws ResponseException {
        Assertions.assertDoesNotThrow(() -> dataAccess.listGames("auth"));
        Assertions.assertNotNull(dataAccess.listGames("auth"));
    }
    @Test
    @DisplayName("invalid list Game Test")
    @Order(11)
    public void emptyListGamesTest() throws ResponseException {
        dataAccess.clear();
        Assertions.assertEquals(new ArrayList<GameData>(), dataAccess.listGames("auth"));
    }
    @Test
    @DisplayName("Update Games Test")
    @Order(12)
    public void UpdateGameTest() throws ResponseException {
        int ID = dataAccess.createGame("user","game");
        Assertions.assertDoesNotThrow(() -> dataAccess.updateGame("auth",new GameData(ID,"white",null,"name")));
        Assertions.assertEquals("white",dataAccess.getGame(ID).whiteUsername());
        Assertions.assertNull(dataAccess.getGame(ID).blackUsername());
        Assertions.assertEquals("name",dataAccess.getGame(ID).gameName());
    }

}

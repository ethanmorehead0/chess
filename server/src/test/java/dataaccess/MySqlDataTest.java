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



/*
        public void createUser(UserData user) throws ResponseException {
            var statement= "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";
            var id = executeUpdate(statement, user.username(), user.password(), user.email());
        }


        //return to here, probably still has lots of problems
        public UserData getUser(String username) throws ResponseException {
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT username, password, email FROM userdata WHERE username=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, username);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return readUser(rs);
                        }
                    }
                }
            } catch (Exception e) {
                throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
            }
            return null;
        }
        public int createGame(String username, String gameName) throws ResponseException {

            var statement= "INSERT INTO gamedata (gameName, game) VALUES (?, ?)";
            var serializer=new Gson();
            ChessGame game = new ChessGame();
            String newGame = serializer.toJson(game);

            return executeUpdate(statement, gameName, newGame);
        }
        public GameData getGame(int gameID) throws ResponseException {
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT id, whiteUsername, blackUsername, gameName FROM gameData WHERE id=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setInt(1, gameID);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return readGame(rs);
                        }
                    }
                }
            } catch (Exception e) {
                throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
            }
            return null;

        }
        public Collection<GameData> listGames(String auth) throws ResponseException {
            var result = new ArrayList<GameData>();
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT id, whiteUsername, blackUsername, gameName FROM gamedata";
                try (var ps = conn.prepareStatement(statement)) {
                    try (var rs = ps.executeQuery()) {
                        while (rs.next()) {
                            result.add(readGame(rs));
                        }
                    }
                }
            } catch (Exception e) {
                throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
            }
            return result;

        }

        public void updateGame(String auth, GameData data) throws ResponseException {
            var statement= "UPDATE gamedata SET whiteUsername=?, blackUsername=?, gameName=? WHERE id=?";
            executeUpdate(statement, data.whiteUsername(), data.blackUsername(), data.gameName(), data.gameID());

        }

        public void createAuth(AuthData auth) throws ResponseException {

            var statement= "INSERT INTO authdata (authtoken, username) VALUES (?, ?)";
            executeUpdate(statement, auth.authToken(), auth.username());


        };

        public AuthData getAuth(String auth) throws ResponseException {
            try (var conn = DatabaseManager.getConnection()) {
                var statement = "SELECT authtoken, username FROM authdata WHERE authtoken=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, auth);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return readAuth(rs);
                        }
                    }
                }
            } catch (Exception e) {
                throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
            }
            return null;
        }
        public void deleteAuth(AuthData auth) throws ResponseException {
            var statement = "DELETE FROM authdata WHERE authtoken=?";
            executeUpdate(statement, auth.authToken());
        }



        private int executeUpdate(String statement, Object... params) throws ResponseException {
            try (var conn = DatabaseManager.getConnection()) {
                try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                    for (var i = 0; i < params.length; i++) {
                        var param = params[i];
                        if (param instanceof String p) ps.setString(i + 1, p);
                        else if (param instanceof Integer p) ps.setInt(i + 1, p);

                        else if (param == null) ps.setNull(i + 1, NULL);
                    }
                    ps.executeUpdate();

                    var rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1);
                    }

                    return 0;
                }
            } catch (SQLException e) {
                throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
            }
        }

        private final String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS `gamedata` (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL DEFAULT 'GAME',
              `json` text,
              PRIMARY KEY (`id`),
              KEY `type` (`blackUsername`),
              KEY `name` (`whiteUsername`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
                """
            CREATE TABLE IF NOT EXISTS `userdata` (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) DEFAULT 'user',
              `email` text,
              PRIMARY KEY (`id`),
              KEY `type` (`password`),
              KEY `name` (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci 
            """,
                """
            CREATE TABLE IF NOT EXISTS `authdata` (
              `auth token` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`auth token`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };


        private void configureDatabase() throws ResponseException {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                for (var statement : createStatements) {
                    try (var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
            }
        }








*/
}

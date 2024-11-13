package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess{
    private int gameNumber=1;
    private ArrayList<UserData> users = new ArrayList<>();
    private HashMap<Integer, GameData> games = new HashMap<>();
    private ArrayList<AuthData> authorization = new ArrayList<>();


    public MySqlDataAccess() throws ResponseException{
        configureDatabase();
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        Integer gameID = rs.getInt("id");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("name");
        return new GameData(gameID, whiteUsername, blackUsername, gameName);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authtoken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }



    public void clear() throws ResponseException {
        executeUpdate("TRUNCATE TABLE userdata");
        executeUpdate("TRUNCATE TABLE gamedata");
        executeUpdate("TRUNCATE TABLE authdata");
    }

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

        //var statement= "SELECT (username) from userdata VALUES (?)";
        //var data = executeUpdate(statement, username);
        //System.out.println(data);
        //select * from name

        /*
        if(users!=null) {
            for (UserData user : users) {
                if (user.username().equals(username)) {
                    return user;
                }
            }
        }
        return null;*/
    }
    public int createGame(String username, String gameName) throws ResponseException {

        var statement= "INSERT INTO gamedata (gameName, game) VALUES (?, ?)";
        String newGame = "Json new game";

        return executeUpdate(statement, gameName, newGame);
    }
    public GameData getGame(int gameID) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM pet WHERE id=?";
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
    public Collection<GameData> listGames(String auth) {
        return games.values();
    }

    public void updateGame(String auth, GameData data) throws ResponseException {
        //only those that are in game can change
        var game = getGame(data.gameID());
        //game=data;
        //games.remove(game);
        //games.put(data.gameID(), data);
        //listGames(auth);

    }

    public void createAuth(AuthData auth) throws ResponseException {

        var statement= "INSERT INTO authdata (authtoken, username) VALUES (?, ?)";
        executeUpdate(statement, auth.authToken(), auth.username());


    };

    public AuthData getAuth(String auth) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM authdata WHERE authtoken=?";
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
        /*
        System.out.print("getAuth: ");
        if(authorization !=null) {
            for (AuthData authorization : authorization) {
                if (authorization.authToken().equals(auth)) {
                    return authorization;
                }
            }
        }
        System.out.println(auth + "  --  " + authorization);
        return null;
        //return new AuthData(null,null);*/
    }
    public void deleteAuth(AuthData auth) throws ResponseException {
        var statement = "DELETE FROM pet WHERE authtoken=?";
        executeUpdate(statement, auth);
        /*if(authorization !=null) {
            System.out.println("First " + authorization);
            authorization.removeIf(authorization -> authorization.equals(auth));
            System.out.println("After " + authorization);
        }*/
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

}

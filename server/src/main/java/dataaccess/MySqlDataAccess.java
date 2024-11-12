package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.w3c.dom.Text;

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

    public void clear() {
        users.clear();
        games.clear();
        authorization.clear();
    }

    public void createUser(UserData user) throws ResponseException {
        var statement= "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";
        var id = executeUpdate(statement, user.username(), user.password(), user.email());
    }
    public UserData getUser(String username) {
        if(users!=null) {
            for (UserData user : users) {
                if (user.username().equals(username)) {
                    return user;
                }
            }
        }
        return null;
    }
    public int createGame(String username, String gameName) throws ResponseException {

        var statement= "INSERT INTO gamedata (gameName, game) VALUES (?, ?)";
        String newGame = "Json new game";
        var id = executeUpdate(statement, gameName, newGame);
        GameData game = new GameData(gameNumber, null, null, gameName);
        games.put(gameNumber, game);
        gameNumber+=1;

        return game.gameID();
    }
    public GameData getGame(int gameID) {
        return games.get(gameID);

    }
    public Collection<GameData> listGames(String auth) {
        System.out.println("1: " + games.values());
        return games.values();
    }

    public void updateGame(String auth, GameData data) {
        //only those that are in game can change
        var game = getGame(data.gameID());
        //game=data;
        games.remove(game);
        games.put(data.gameID(), data);
        //listGames(auth);

    }

    public void createAuth(AuthData auth) {
        authorization.add(auth);
    };

    public AuthData getAuth(String auth) {
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
        //return new AuthData(null,null);
    }
    public void deleteAuth(AuthData auth) {
        if(authorization !=null) {
            System.out.println("First " + authorization);
            authorization.removeIf(authorization -> authorization.equals(auth));
            System.out.println("After " + authorization);
        }
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
            CREATE TABLE IF NOT EXISTS `userdata` (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `password` varchar(256) DEFAULT 'user',
              `email` text,
              PRIMARY KEY (`id`),
              KEY `type` (`password`),
              KEY `name` (`name`)
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

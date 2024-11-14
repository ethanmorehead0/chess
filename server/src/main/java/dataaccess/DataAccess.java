package dataaccess;

import exception.ResponseException;
import model.*;

import java.util.Collection;

public interface DataAccess {
    void clear() throws ResponseException;
    void createUser(UserData user) throws ResponseException;
    UserData getUser(String username) throws ResponseException;
    int createGame(String username, String gameName) throws ResponseException;
    GameData getGame(int game) throws ResponseException;
    Collection<GameData> listGames(String auth) throws ResponseException;
    void updateGame(String auth, GameData data) throws ResponseException;
    void createAuth(AuthData auth) throws ResponseException;
    AuthData getAuth(String auth) throws ResponseException;
    void deleteAuth(AuthData authToken) throws ResponseException;
    boolean checkPassword(String username, String password) throws ResponseException;
}

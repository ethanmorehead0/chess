package dataaccess;

import exception.ResponseException;
import model.*;

import java.util.Collection;

public interface DataAccess {
    void clear() throws ResponseException;
    void createUser(UserData user) throws ResponseException;
    UserData getUser(String username) throws ResponseException;
    GameData createGame(String username, String gameName) throws ResponseException;
    CreateGameResult getGame(int game) throws ResponseException;
    Collection<GameData> listGames(String auth) throws ResponseException;
    void updateGame() throws ResponseException;
    void createAuth(AuthData auth) throws ResponseException;
    AuthData getAuth(String auth) throws ResponseException;
    void deleteAuth(AuthData authToken) throws ResponseException;

}

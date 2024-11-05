package dataaccess;

import exception.ResponseException;
import model.*;

import java.util.Collection;

public interface DataAccess {
    void clear() throws ResponseException;
    void createUser(UserData user) throws ResponseException;
    UserData getUser(String username) throws ResponseException;
    void createGame() throws ResponseException;
    GameData getGame() throws ResponseException;
    Collection<GameData> listGames(String auth) throws ResponseException;
    void updateGame() throws ResponseException;
    void createAuth(AuthData auth) throws ResponseException;
    AuthData getAuth() throws ResponseException;
    void deleteAuth(String authToken) throws ResponseException;




    //wrong...
    AuthData login() throws ResponseException;
}

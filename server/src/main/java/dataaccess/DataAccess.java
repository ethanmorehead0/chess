package dataaccess;

import exception.ResponseException;
import model.*;

public interface DataAccess {
    void clear() throws ResponseException;
    void createUser() throws ResponseException;
    UserData getUser() throws ResponseException;
    void createGame() throws ResponseException;
    GameData getGame() throws ResponseException;
    AllGamesData listGames() throws ResponseException;
    void updateGame() throws ResponseException;
    void createAuth() throws ResponseException;
    AuthData getAuth() throws ResponseException;
    void deleteAuth() throws ResponseException;




    //wrong...
    AuthData login() throws ResponseException;
}

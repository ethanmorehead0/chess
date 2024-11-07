package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.*;


import java.util.ArrayList;
import java.util.Collection;

public class MemoryDataAccess implements DataAccess{
    private int gameNumber=0;
    private ArrayList<UserData> users = new ArrayList<>();
    private ArrayList<GameData> games = new ArrayList<>();
    private ArrayList<AuthData> Authorization = new ArrayList<>();


    public void clear() {
        users.clear();
        games.clear();
        Authorization.clear();
    };

    public void createUser(UserData user) {
        users.add(user);
    };
    public UserData getUser(String username) {
        if(users!=null) {
            for (UserData user : users) {
                if (user.username().equals(username)) {
                    return user;
                }
            }
        }
        return null;
    };
    public GameData createGame(String username, String gameName) {

        GameData game = new GameData(gameNumber, username, null, gameName, new ChessGame());
        gameNumber+=1;
        games.add(game);
        return game;
    };
    public CreateGameResult getGame(int game) {

        return new CreateGameResult(game);
        //return new GameData(1, "a", "b", "c", new ChessGame());
    };
    public Collection<GameData> listGames(String auth) {
        return games;
    };

    public void updateGame() {

    };

    public void createAuth(AuthData auth) {
        Authorization.add(auth);
    };
    public AuthData getAuth(String auth) {
        if(Authorization!=null) {
            for (AuthData authorization : Authorization) {
                if (authorization.authToken().equals(auth)) {
                    return authorization;
                }
            }
        }
        return null;
    };
    public void deleteAuth(AuthData auth) {
        if(Authorization!=null) {
            Authorization.removeIf(authorization -> authorization.equals(auth));
        }
    };

}

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

    public void createUser(UserData user) throws ResponseException{
        users.add(user);
    };
    public UserData getUser(String username) throws ResponseException{
        if(users!=null) {
            for (UserData user : users) {
                if (user.username().equals(username)) {
                    return user;
                }
            }
        }
        return null;
    };
    public GameData createGame(String username, String gameName) throws ResponseException{

        GameData game = new GameData(gameNumber, username, null, gameName, new ChessGame());
        gameNumber+=1;
        games.add(game);
        return game;
    };
    public CreateGameResult getGame(int game) throws ResponseException{

        return new CreateGameResult(game);
        //return new GameData(1, "a", "b", "c", new ChessGame());
    };
    public Collection<GameData> listGames(String auth) throws ResponseException{
        return games;
    };

    public void updateGame() throws ResponseException{

    };

    public void createAuth(AuthData auth) throws ResponseException{
        Authorization.add(auth);
    };
    public AuthData getAuth(String auth) throws ResponseException{
        if(Authorization!=null) {
            for (AuthData authorization : Authorization) {
                if (authorization.authToken().equals(auth)) {
                    return authorization;
                }
            }
        }
        return null;
    };
    public void deleteAuth(AuthData auth) throws ResponseException{
        if(Authorization!=null) {
            Authorization.removeIf(authorization -> authorization.equals(auth));
        }
    };

}

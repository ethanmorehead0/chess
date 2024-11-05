package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.AllGamesData;
import model.AuthData;
import model.GameData;
import model.UserData;


import java.util.ArrayList;
import java.util.Collection;

public class MemoryDataAccess implements DataAccess{
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
    public void createGame() throws ResponseException{

    };
    public GameData getGame() throws ResponseException{
        return new GameData(0, "a", "b", "c", new ChessGame());
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





    //toDelete later...
    public AuthData login(){
        AuthData auth= new AuthData("temp", "temp");

        //implement


        return auth;
    }

}

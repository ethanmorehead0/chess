package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.AllGamesData;
import model.AuthData;
import model.GameData;
import model.UserData;


import java.util.ArrayList;

public class MemoryDataAccess implements DataAccess{
    private ArrayList<UserData> users = new ArrayList<>();
    private ArrayList<GameData> games = new ArrayList<>();
    private ArrayList<AuthData> Authorization = new ArrayList<>();


    public void clear() {
        users.clear();
        games.clear();
        Authorization.clear();
    };

    public void createUser() throws ResponseException{

    };
    public UserData getUser() throws ResponseException{
        return new UserData();
    };
    public void createGame() throws ResponseException{

    };
    public GameData getGame() throws ResponseException{
        return new GameData(0, "a", "b", "c", new ChessGame());
    };
    public AllGamesData listGames() throws ResponseException{
        return new AllGamesData(0, "a", "b", "c", new ChessGame());
    };
    public void updateGame() throws ResponseException{

    };
    public void createAuth() throws ResponseException{

    };
    public AuthData getAuth() throws ResponseException{
        return new AuthData("a", "b");
    };
    public void deleteAuth() throws ResponseException{

    };





    //toDelete later...
    public AuthData login(){
        AuthData auth= new AuthData("temp", "temp");

        //implement


        return auth;
    }

}

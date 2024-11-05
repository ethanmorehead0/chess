package service;
import dataaccess.DataAccess;
import exception.ResponseException;
import model.*;

import java.util.Collection;


public class ChessService {

    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //All

    public void Clear() throws ResponseException {
        dataAccess.clear();
    }



    public AuthData Login(LoginRequest req) throws ResponseException {
        AuthData auth = dataAccess.login();
        return auth;
    }
    public void Logout(LogoutRequest req) throws ResponseException {
        if(req==null){
            throw new ResponseException(500, "no user logged in");
        }else{
            dataAccess.deleteAuth(req.auth());

        }
    }


    public AuthData register(UserData req) throws ResponseException {
        return new AuthData("a","b");
    }

    public Collection<GameData> ListGames(String auth) throws ResponseException {
        return dataAccess.listGames(auth);
    }

    public String CreateGame() throws ResponseException{
        return "gameID";
    }

    public void JoinGame() throws ResponseException{

    }



}

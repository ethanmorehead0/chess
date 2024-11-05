package service;
import dataaccess.DataAccess;
import exception.ResponseException;
import model.*;


public class ChessService {

    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //All

    public void Clear() throws ResponseException {
        dataAccess.clear();
    }



    //AuthService
    public AuthData Login(LoginRequest req) throws ResponseException {
        AuthData auth = dataAccess.login();
        return auth;
    }
    public void Logout(LogoutRequest req) throws ResponseException {
        dataAccess.deleteAuth();
    }


    //GameService




    //UserService



}

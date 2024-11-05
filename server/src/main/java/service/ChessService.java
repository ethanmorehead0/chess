package service;
import dataaccess.DataAccess;
import exception.ResponseException;
import model.AuthData;
import model.LoginRequest;


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


    //GameService




    //UserService



}

package service;
import dataaccess.DataAccess;
import exception.ResponseException;


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



    //GameService




    //UserService



}

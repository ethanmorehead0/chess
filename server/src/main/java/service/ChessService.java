package service;
import dataaccess.DataAccess;
import exception.ResponseException;
import model.*;

import java.util.Collection;
import java.util.UUID;


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
        UserData user=dataAccess.getUser(req.username());

        if(user!=null && user.password().equals(req.password())){
            return new AuthData(CreateAuth(),user.username());
        }
        else{
            throw new ResponseException(401,"Error: unauthorized");
        }
        /*AuthData auth = dataAccess.login();
        return auth;*/
    }
    public void Logout(String req) throws ResponseException {
        if(req!=null){
            if(dataAccess.getAuth(req)==null){
                throw new ResponseException(401, "Error: unauthorized");
            }
            dataAccess.deleteAuth(dataAccess.getAuth(req));
        }else{

            throw new ResponseException(401, "Error: unauthorized");
        }
    }


    public AuthData register(UserData user) throws ResponseException {
        //need to handle exceptions
        UserData userData = dataAccess.getUser(user.username());
        if(userData != null){
            throw new ResponseException(403, "User already registered");
        } else if (user.password()==null || user.username()==null || user.email()==null) {
            throw new ResponseException(400, "Bad request");
        }
        dataAccess.createUser(user);
        AuthData data=new AuthData(CreateAuth(),user.username());
        dataAccess.createAuth(data);

        return data;
    }

    public Collection<GameData> ListGames(String auth) throws ResponseException {
        return dataAccess.listGames(auth);
    }

    public String CreateGame() throws ResponseException{
        return "gameID";
    }

    public void JoinGame() throws ResponseException{

    }




    private String CreateAuth(){
        return UUID.randomUUID().toString();
    }

}

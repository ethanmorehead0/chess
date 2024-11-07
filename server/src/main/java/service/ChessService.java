package service;
import chess.ChessGame;
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
            return new AuthData(CreateAuthID(),user.username());
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
        AuthData data=new AuthData(CreateAuthID(),user.username());
        dataAccess.createAuth(data);

        return data;
    }

    public Collection<GameData> ListGames(String auth) throws ResponseException {
        /*if(dataAccess.getAuth(auth)==null){
            throw new ResponseException(401, "Error: unauthorized");
        }*/

        return dataAccess.listGames(auth);
    }

    public CreateGameResult CreateGame(String authToken, String name) throws ResponseException{
        AuthData auth = dataAccess.getAuth(authToken);
        if(auth==null){
            throw new ResponseException(401,"Error: unauthorized");
        }
        return new CreateGameResult(dataAccess.createGame(auth.username(), name));
    }

    public void JoinGame(String authToken, JoinGameRequest req) throws ResponseException{
        AuthData auth = dataAccess.getAuth(authToken);
        if(auth==null){
            throw new ResponseException(401,"Error: unauthorized");
        }
        GameData data = dataAccess.getGame(req.gameID());
        if(data==null || (req.color() == ChessGame.TeamColor.BLACK && data.blackUsername()!=null) || (req.color() == ChessGame.TeamColor.WHITE && data.whiteUsername()!=null)){
            throw new ResponseException(403,"Error: already taken");
        }
        GameData newData= new GameData(data.gameID(),data.whiteUsername(),auth.username(),data.gameName(),data.game());

        dataAccess.updateGame(authToken,newData);

    }




    private String CreateAuthID(){
        return UUID.randomUUID().toString();
    }

}

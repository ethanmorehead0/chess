package service;
import chess.ChessGame;
import dataaccess.DataAccess;
import exception.ResponseException;
import model.*;
import websocket.commands.UserGameCommand;

import java.util.ArrayList;
import java.util.UUID;


public class ChessService {

    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //All

    public void clear() throws ResponseException {
        dataAccess.clear();
    }



    public AuthData login(LoginRequest req) throws ResponseException {
        UserData user=dataAccess.getUser(req.username());

        if(user!=null && dataAccess.checkPassword(req.username(), req.password())){
            String newAuth= createAuthID();
            dataAccess.createAuth(new AuthData(newAuth, user.username()));
            return new AuthData(newAuth, user.username());
        }
        else{
            throw new ResponseException(401,"unauthorized");
        }
    }
    public void logout(String req) throws ResponseException {
        if(req!=null){
            if(dataAccess.getAuth(req)==null){
                throw new ResponseException(401, "unauthorized");
            }
            dataAccess.deleteAuth(dataAccess.getAuth(req));
        }else{

            throw new ResponseException(401, "unauthorized");
        }
    }

    public void leave(String authToken, LeaveGameRequest req) throws ResponseException {
        AuthData auth = dataAccess.getAuth(authToken);

        if(req.playerColor() == null || req.gameID() == null){
            throw new ResponseException(400,"unauthorized");
        }
        if(auth==null){
            throw new ResponseException(401,"unauthorized");
        }

        GameData data = dataAccess.getGame(req.gameID());
        if(data==null){
            throw new ResponseException(403,"username already taken");
        }
        if((req.playerColor().equals(ChessGame.TeamColor.BLACK) && data.blackUsername()==null)){
            throw new ResponseException(403,"username already taken");
        }
        if((req.playerColor() == ChessGame.TeamColor.WHITE && data.whiteUsername()==null)){
            throw new ResponseException(403,"username already taken");
        }

        GameData newData;
        if(req.playerColor()== ChessGame.TeamColor.WHITE){
            newData= new GameData(data.gameID(), null, data.blackUsername(), data.gameName());
        }else{
            newData= new GameData(data.gameID(), data.whiteUsername(), null, data.gameName());
        }

        dataAccess.updateGame(authToken, newData);
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
        AuthData data=new AuthData(createAuthID(),user.username());
        dataAccess.createAuth(data);

        return data;
    }

    public AllGamesData listGames(String auth) throws ResponseException {
        if(dataAccess.getAuth(auth)==null){
            throw new ResponseException(401, "unauthorized");
        }

        return new AllGamesData((ArrayList<GameData>) dataAccess.listGames(auth));
    }

    public CreateGameResult createGame(String authToken, String name) throws ResponseException{
        AuthData auth = dataAccess.getAuth(authToken);
        if(auth==null){
            throw new ResponseException(401,"unauthorized");
        }
        return new CreateGameResult(dataAccess.createGame(auth.username(), name));
    }

    public void joinGame(String authToken, JoinGameRequest req) throws ResponseException{
        AuthData auth = dataAccess.getAuth(authToken);

        if(req.playerColor() == null || req.gameID() == null){
            throw new ResponseException(400,"unauthorized");
        }
        if(auth==null){
            throw new ResponseException(401,"unauthorized");
        }

        GameData data = dataAccess.getGame(req.gameID());
        if(data==null){
            throw new ResponseException(403,"username already taken");
        }
        if((req.playerColor().equals(ChessGame.TeamColor.BLACK) && data.blackUsername()!=null)){
            throw new ResponseException(403,"username already taken");
        }
        if((req.playerColor() == ChessGame.TeamColor.WHITE && data.whiteUsername()!=null)){
            throw new ResponseException(403,"username already taken");
        }

        GameData newData;
        if(req.playerColor()== ChessGame.TeamColor.WHITE){
            newData= new GameData(data.gameID(), auth.username(), data.blackUsername(), data.gameName());
        }else{
            newData= new GameData(data.gameID(), data.whiteUsername(), auth.username(), data.gameName());
        }

        dataAccess.updateGame(authToken, newData);
    }

    public boolean connect(UserGameCommand command) throws ResponseException{
        try {
            AuthData auth = dataAccess.getAuth(command.getAuthToken());
            if (command.getGameID() == null || command.getAuthToken() == null) {
                throw new ResponseException(401, "unauthorized");
            }

            GameData data = dataAccess.getGame(command.getGameID());
        }catch(ResponseException exception){
            System.out.println(exception.getMessage());
            return false;
        }
        return true;
        //check listed games
        //check auth
    }
    public void makeMove(String authToken, String name) throws ResponseException{

    }
    public void leaveGame(String authToken, String name) throws ResponseException{

    }
    public void resignGame(String authToken, String name) throws ResponseException{

    }



    private String createAuthID(){
        return UUID.randomUUID().toString();
    }

}

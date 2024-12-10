package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.MySqlDataAccess;
import exception.ResponseException;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import service.ChessService;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;
import java.util.ArrayList;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    ChessService service;

    public WebSocketHandler() throws ResponseException {
        service=new ChessService(new MySqlDataAccess());
    }
    public WebSocketHandler(ChessService service) {
        this.service = service;
    }

/*
    @OnWebSocketConnect
    public void onConnect(Session session){

    }
    @OnWebSocketClose
    public void onClose(Session session){

    }
    @OnWebSocketError
    public void onError(Throwable throwable){

    }
*/


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResponseException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class), session);
            case LEAVE -> leave(command, session);
            case RESIGN -> resign(command, session);
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException, ResponseException {

        if(!service.canConnect(command)){
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "message");
            sendMessage(error, session);
            return;
        }

        connections.addSessionToGame(command.getGameID(), session);
        //if statement that checks to see if game exists if not then send server message of type error.
        //if
        var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, loadGame(command.getGameID(), command.getAuthToken()));
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "message");
        sendMessage(loadGame, session);
        broadcastMessage(command.getGameID(), notification, session);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException, ResponseException {
        try{
            service.makeMove(command);
        }catch(Exception exception){
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "\n"+exception.getMessage()+"\n"+exception.getLocalizedMessage()+"\n");
            sendMessage(error, session);
            return;
        }
        var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, "game");
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, service.getName(command.getAuthToken())+ "joined the game");
        sendMessage(loadGame, session);
        broadcastMessage(command.getGameID(), loadGame, session);
        broadcastMessage(command.getGameID(), notification, session);

    }

    public void leave(UserGameCommand command, Session session) throws IOException {
        //connections.removeSessionFromGame(authToken);
        ChessGame.TeamColor team=null;
        try {
            team = service.leaveGame(command);
        }catch (Exception exception){
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "\n"+exception.getMessage()+"\n"+exception.getLocalizedMessage()+"\n");
            sendMessage(error, session);
            return;
        }

        var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, "game");
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "leave");

        //if player is white/black
        //if player is an observer
        /*if(team!=null){
            broadcastMessage(command.getGameID(), notification, session);
        }
        else{sendMessage(notification, session);}*/
    }

    public void resign(UserGameCommand command, Session session) throws ResponseException, IOException {
        try{
        service.resignGame(command);
        }
        catch(Exception exception){
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Can not resign");
            sendMessage(error, session);
            return;
        }
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "leave");
        sendMessage(notification, session);
        broadcastMessage(command.getGameID() ,notification, session);
    }
    public void sendMessage(ServerMessage message, Session session) throws IOException {
        var serializer=new Gson();
        session.getRemote().sendString(serializer.toJson(message));
    }
    public void broadcastMessage(int gameID, ServerMessage message, Session exceptThisSession) throws IOException {
        ArrayList<Session> sessions = this.connections.getSessionsForGame(gameID);
        var removeList = new ArrayList<Session>();
        for(Session session:sessions){
            if(session.isOpen()) {
                if (!session.equals(exceptThisSession)) {
                    var serializer = new Gson();
                    session.getRemote().sendString(serializer.toJson(message));
                }
            } else {
                removeList.add(session);
            }
        }

        for (var session : removeList) {
            connections.removeSessionFromGame(gameID, session);
        }

    }
    public String loadGame(int gameID, String auth)throws ResponseException{
        AllGamesData games = service.listGames(auth);
        var serializer=new Gson();
        for (GameData game:games.games()){
            if(game.gameID()==gameID){
                return serializer.toJson(game);
            }
        }
        throw new ResponseException(401,"error: no game found");
    }

}
package server.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;
import java.util.ArrayList;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResponseException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave(command.getAuthToken());
            case RESIGN -> resign();
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        /*if(!connections.connections.containsKey(command.getGameID())){
            connections.broadcast(command.getGameID(), new LoadGameMessage(ServerMessage.ServerMessageType.ERROR));
            return;
        }*/
        connections.addSessionToGame(command.getGameID(), session);

        //if statement that checks to see if game exists if not then send server message of type error.
        //if
        var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, "game");
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "message");
        sendMessage(loadGame, session);
        broadcastMessage(1, notification, session);
    }

    private void makeMove() throws ResponseException {

    }

    public void leave(String authToken) throws IOException {
        //connections.removeSessionFromGame(authToken);

        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        //connections.broadcast(command.getGameID, notification);
    }

    public void resign() throws ResponseException {

    }
    public void sendMessage(ServerMessage message, Session session) throws IOException {
        var serializer=new Gson();
        session.getRemote().sendString(serializer.toJson(message));
    }
    public void broadcastMessage(int gameID, ServerMessage message, Session exceptThisSession) throws IOException {
        ArrayList<Session> sessions = connections.getSessionsForGame(gameID);
        for(Session session:sessions){
            if(!session.equals(exceptThisSession)) {
                var serializer = new Gson();
                session.getRemote().sendString(serializer.toJson(message));
            }
        }



        /*var removeList = new ArrayList<Connection>();
        ArrayList<Session> broadcastToSessions = connections.get(gameID);
        var serializer = new Gson();
        for (Session session: broadcastToSessions){
            //session.send(serializer.toJson(message));
            session.
        }
        for (var c : connections.get(gameID)) {
            System.out.println(c);
            if (c.session.isOpen()) {
                if (!c.authData.equals(excludeVisitorName)) {
                    var serializer = new Gson();
                    c.send(serializer.toJson(message));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authData);
        }*/
    }

}
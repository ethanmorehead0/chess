package server.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResponseException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command.getAuthToken(), session);
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    private void connect(String authToken, Session session) throws IOException {
        connections.add(authToken, session);
        //var message = String.format("%s is in the shop", authToken);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(null, notification);

    }

    private void makeMove() throws ResponseException {

    }

    public void leave() throws IOException {

    }

    public void resign() throws ResponseException {

    }
}
package ui;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;



    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    switch(notification.getServerMessageType()){
                        case LOAD_GAME:
                            ServerMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            notificationHandler.notify(loadGameMessage);
                            break;
                        case ERROR:
                            ServerMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                            notificationHandler.notify(errorMessage);
                            break;
                        case NOTIFICATION:
                            ServerMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notificationMessage);
                            break;
                        default:
                            notificationHandler.notify(notification);
                    }

                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void joinChessGame(String authToken, int gameID) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void leaveChessGame(String authToken, int gameID) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));

        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            var action = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));

        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void getBoard(String authToken, int gameID) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.BOARD, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));

        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    public void resignChessGame(String authToken, int gameID) throws ResponseException {
        try{
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        }catch (IOException ex){
            throw new ResponseException(500, ex.getMessage());
        }
    }


}


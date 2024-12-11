package server.websocket;

import com.google.gson.Gson;
import model.AuthData;
import model.LoginRequest;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Session>> connections = new ConcurrentHashMap<>();

    public void addSessionToGame(int gameID, Session session) {
        if(!connections.containsKey(gameID)) {
            connections.put(gameID,new ArrayList<Session>());
        }
        connections.get(gameID).add(session);
    }

    public void removeSessionFromGame(int gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public ArrayList<Session> getSessionsForGame(int gameID) {
        return connections.get(gameID);
    }


}

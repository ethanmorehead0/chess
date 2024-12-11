package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String authData;
    public Session session;

    public Connection(String authData, Session session) {
        this.authData = authData;
        this.session = session;
    }

}
package websocket.messages;

public class NotificationMessage extends ServerMessage{
    public String message;
    public NotificationMessage(ServerMessageType type) {
        super(type);
    }
    public NotificationMessage(ServerMessageType type, String game){
        super (type);
        this.message=game;
    }
}

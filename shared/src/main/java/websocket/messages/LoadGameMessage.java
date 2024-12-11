package websocket.messages;

public class LoadGameMessage extends ServerMessage{
    public String game;
    public LoadGameMessage(ServerMessageType type) {
        super(type);
    }
    public LoadGameMessage(ServerMessageType type, String game){
        super (type);
        this.game=game;
    }
    @Override
    public String toString() {


        return game;
    }
}

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import ui.*;
import static ui.EscapeSequences.*;
import websocket.messages.*;
import java.util.Scanner;


public class Repl implements NotificationHandler{
    private final ChessClient chessClient;

    public Repl(String serverUrl) {
        chessClient = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("Welcome to Chess. Login to start.");

        System.out.print(SET_TEXT_COLOR_BLUE + chessClient.preloginHelp());

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = chessClient.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }

        System.out.println();

    }

    public void notify(ServerMessage message){
        if(message.getServerMessageType()== ServerMessage.ServerMessageType.LOAD_GAME){
            ChessGame game = new Gson().fromJson(message.toString(), ChessGame.class);
            try {
                System.out.print("\b\b\b\b" + chessClient.printBoard(game.getBoard()));
                printPrompt();
                return;
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("\b\b\b\b" + SET_TEXT_COLOR_BLUE + message.toString());
        printPrompt();
    }


    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}

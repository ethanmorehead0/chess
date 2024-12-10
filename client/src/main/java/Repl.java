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

    /*public void notify(LoadGameMessage message){
        System.out.println(SET_TEXT_COLOR_BLUE + message);
        printPrompt();
    }
    public void notify(NotificationMessage message){
        System.out.println(SET_TEXT_COLOR_BLUE + message);
        printPrompt();
    }
    public void notify(ErrorMessage message){
        System.out.println(SET_TEXT_COLOR_RED + message);
        printPrompt();
    }*/
    public void notify(ServerMessage message){
        System.out.println("1. " + message);
        System.out.println(message.getServerMessageType());
        printPrompt();
    }


    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}

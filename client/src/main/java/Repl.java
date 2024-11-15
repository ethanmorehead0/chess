import ui.*;
import static ui.EscapeSequences.*;

import java.util.Scanner;


public class Repl{
    private final ChessClient chessClient;

    public Repl(String serverUrl) {
        chessClient = new ChessClient(serverUrl);
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



    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}

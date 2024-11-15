import ui.*;
import static ui.EscapeSequences.*;

import java.util.Scanner;


public class Repl{
    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    //private final GameplayClient gameplayClient;

    public Repl(String serverUrl) {
        preloginClient = new PreloginClient(serverUrl);
        postloginClient = new PostloginClient(serverUrl);
        //gameplayClient = new GameplayClient(serverUrl);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to the Chess. Sign in to start.");

        System.out.print(SET_TEXT_COLOR_BLUE + preloginClient.help()[1]);

        Scanner scanner = new Scanner(System.in);
        String[] result = {"", ""};
        String client="prelogin";

        while (!result[0].equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                switch (client) {
                    case "prelogin" -> {
                        result = preloginClient.eval(line);
                        client = result[0];
                    }
                    case "postLogin" -> {
                        result = postloginClient.eval(line);
                        client = result[0];
                    }
                    case "game" -> {

                    }
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result[1]);

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
                System.out.print(msg);
                System.out.print(msg.getClass());
            }
        }

        System.out.println();

    }



    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}

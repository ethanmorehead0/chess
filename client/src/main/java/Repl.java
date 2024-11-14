import ui.*;

import java.util.Scanner;

public class Repl{
    private final PreloginClient preloginClient;

    public Repl(String serverUrl) {
        preloginClient = new PreloginClient();}

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to the Chess. Sign in to start.");
/*
        System.out.print(preloginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = preloginClient.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    */
    }



    private void printPrompt() {

    }

}

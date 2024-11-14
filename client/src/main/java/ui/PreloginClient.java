package ui;

import java.util.Arrays;

import com.google.gson.Gson;
import exception.ResponseException;
import server.ServerFacade;

public class PreloginClient {
    private final ServerFacade server;


    public PreloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login", "l" -> login(params);
                case "register", "r" -> register(params);
                case "quit", "q" -> quit();
                case "help", "h" -> help();
                default -> help();
            };

        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }



    public String login(String... s) throws ResponseException{
        return "signIn";
    }
    public String register(String... s) throws ResponseException{
        return "register";
    }
    public String quit() throws ResponseException{
        return "quit";
    }
    public String help() {
        return """
            Options:
            - Login: "l", "login" <USERNAME> <PASSWORD>
            - Register: "r", "register" <USERNAME> <PASSWORD> <EMAIL>
            - Exit/quit: "q", "quit"
            - Help: "h", "help"
            """;
    }

}

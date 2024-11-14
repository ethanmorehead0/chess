
package ui;

import java.util.Arrays;

import com.google.gson.Gson;
import exception.ResponseException;
import server.ServerFacade;

public class PostloginClient {
    private final ServerFacade server;


    public PostloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                //case "signin" -> signIn(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }



    public String help() throws ResponseException {
        return """
            Options:
            - List current games: "l", "list"
            - Create a new game: "c", "create" <GAME NAME>
            - Join a game: "j", "join" <GAME ID> <COLOR>
            - Watch a game: "Logout"
            - Help: "h", "help"
            """;
    }

}

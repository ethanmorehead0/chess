
package ui;

import java.util.Arrays;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import server.ServerFacade;

public class PostloginClient {
    private final ServerFacade server;


    public PostloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String[] eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "list", "l" -> list();
                case "create", "c" -> create(params);
                /*case "join", "j" -> join();
                case "watch", "w" -> watch();
                case "logout" -> logout();
                case "help", "h" -> help();*/
                default -> help();
            };
        } catch (ResponseException ex) {
            return new String[]{"postLogin", ex.getMessage()};
        }
    }

    public String[] list() throws ResponseException{
        var output = server.listGames();
        return new String[]{"postLogin","Games: \n" + output};
    }

    public String[] create(String... params) throws ResponseException{
        if (params.length >= 1) {
            var output = server.createGame(new CreateGameRequest(params[0]));
            return new String[]{"postLogin","Games: \n"};
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String[] help() throws ResponseException {
        return new String[]{"postLogin","""
            Options:
            - List current games: "l", "list"
            - Create a new game: "c", "create" <GAME NAME>
            - Join a game: "j", "join" <GAME ID> <COLOR>
            - Watch a game: "logout"
            - Help: "h", "help"
            """};
    }

}

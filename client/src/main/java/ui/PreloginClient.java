package ui;

import java.util.Arrays;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import server.ServerFacade;

public class PreloginClient {
    private final ServerFacade server;


    public PreloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String[] eval(String input) {
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
            return new String[]{"prelogin", ex.getMessage()};
        }
    }



    public String[] login(String... params) throws ResponseException{
        if (params.length >= 2) {
            var output = server.login(new LoginRequest(params[0],params[1]));
            return new String[]{"postLogin","Welcome " + params[0]};
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");


    }
    public String[] register(String... params) throws ResponseException{
        if (params.length >= 3) {
            var output = server.registration(new UserData(params[0], params[1], params[2]));

            return new String[]{"postLogin","Welcome " + params[0] + "/n" + help()[1]};
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }
    public String[] quit() throws ResponseException{
        return new String[]{"quit",""};
    }
    public String[] help() {
        return new String[]{"prelogin","""
            Options:
            - Login: "l", "login" <USERNAME> <PASSWORD>
            - Register: "r", "register" <USERNAME> <PASSWORD> <EMAIL>
            - Exit/quit: "q", "quit"
            - Help: "h", "help"
            """};
    }

}

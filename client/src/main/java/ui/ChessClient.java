package ui;

import java.util.Arrays;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import server.ServerFacade;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private AuthData auth;
    private String stage="preLogin";
    private int gameID = -1;

    public ChessClient(String serverUrl) {
        auth=new AuthData(null, null);
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input){
        String result="";
        switch (stage) {
            case "preLogin" -> {
                result = preloginEval(input);
            }
            case "postLogin" -> {
                result = postLoginEval(input);
            }
            case "game" -> {
                result = gameEval(input);
            }
        }
        return result;
    }





    //PreLogin
    public String preloginEval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login", "l" -> login(params);
                case "register", "r" -> register(params);
                case "quit", "q" -> quit();
                case "help", "h" -> preloginHelp();
                default -> preloginHelp();
            };

        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    public String login(String... params) throws ResponseException{
        if (params.length >= 2) {
            var output = server.login(new LoginRequest(params[0],params[1]));
            auth=output;
            stage="postLogin";
            return "Welcome " + output.username();
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");


    }
    public String register(String... params) throws ResponseException{
        if (params.length >= 3) {
            var output = server.registration(new UserData(params[0], params[1], params[2]));
            auth=output;
            stage="postLogin";
            return "Welcome " + params[0];
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }
    public String quit() throws ResponseException{
        return "quit";
    }
    public String preloginHelp() {
        return """
            Options:
            - Login: "l", "login" <USERNAME> <PASSWORD>
            - Register: "r", "register" <USERNAME> <PASSWORD> <EMAIL>
            - Exit/quit: "q", "quit"
            - Help: "h", "help"
            """;
    }




    //PostLogin
    public String postLoginEval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "list", "l" -> list();
                case "create", "c" -> create(params);
                case "join", "j" -> join(params);
                /*case "watch", "w" -> watch();*/
                case "logout" -> logout();
                case "help", "h" -> postLoginHelp();
                default -> postLoginHelp();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String list() throws ResponseException{
        var output = server.listGames();
        return "Games: \n" + output;
    }

    public String create(String... params) throws ResponseException{
        if (params.length >= 1) {
            var output = server.createGame(new CreateGameRequest(params[0]));
            return "Created: '"+params[0]+"'";
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String join(String... params) throws ResponseException{
        if (params.length >= 2) {
            if(params[1].equalsIgnoreCase("black")){
                gameID = server.joinGame(new JoinGameRequest(ChessGame.TeamColor.BLACK, Integer.parseInt(params[0])));
            }else if(params[1].equalsIgnoreCase("white")){
                gameID = server.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, Integer.parseInt(params[0])));
            }else{
                throw new ResponseException(400,"Expected: <GAME ID> <COLOR>");
            }
            stage = "game";
            return "Joined: '"+params[0]+"'";
        }
        throw new ResponseException(400, "Expected: <GAME ID> <COLOR>");
    }
    public String watch(String... params) throws ResponseException{
        stage="game";
        return "Watching: '"+params[0]+"'";

    }

    public String logout() throws ResponseException{
        server.logout();
        stage="preLogin";
        auth=new AuthData(null, null);
        return "Logged out";

    }


    public String postLoginHelp() throws ResponseException {
        return """
            Options:
            - List current games: "l", "list"
            - Create a new game: "c", "create" <GAME NAME>
            - Join a game: "j", "join" <GAME ID> <COLOR>
            - Watch a game: "w", "watch" <GAME ID>
            - Logout: "logout"
            - Help: "h", "help"
            """;
    }



    public String printBoard(ChessGame.TeamColor color) throws ResponseException {
        ChessGame game = new ChessGame();
        String[][] toPrint ={
                {SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + EMPTY, " A\u2003", " B\u2003", " C\u2003", " D\u2003", " E\u2003", " F\u2003", " G\u2003", " H\u2003", SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + EMPTY},

                {SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + EMPTY, " A\u2003", " B\u2003", " C\u2003", " D\u2003", " E\u2003", " F\u2003", " G\u2003", " H\u2003", SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + EMPTY}
        };

        StringBuilder output= new StringBuilder();

        for(String[] rows:toPrint){
            for(String space: rows){
                if(color== ChessGame.TeamColor.BLACK) {
                    output.append(space);
                }else {
                    output.insert(0, space);
                }
            }
            if(color== ChessGame.TeamColor.BLACK) {
                output.append(RESET_BG_COLOR + "\n");
            }else {
                output.insert(0, RESET_BG_COLOR + "\n");
            }
        }

        return output.toString() + RESET_BG_COLOR;
    }

    public String gameHelp() {
        return """
            Options:
            - Print White: "w", "white"
            - Print Black: "b", "register"
            - Help: "h", "help"
            """;
    }
    public String gameEval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "white", "w" -> printBoard(ChessGame.TeamColor.WHITE);
                case "black", "b" -> printBoard(ChessGame.TeamColor.BLACK);
                case "help", "h" -> gameHelp();
                default -> postLoginHelp();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

}

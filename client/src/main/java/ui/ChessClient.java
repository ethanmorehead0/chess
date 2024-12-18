package ui;

import java.util.ArrayList;
import java.util.Arrays;

import chess.*;
import exception.ResponseException;
import model.*;
import server.ServerFacade;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private String serverUrl;
    private WebSocketFacade ws;
    private final NotificationHandler notificationHandler;
    private AuthData auth;
    private String stage="preLogin";
    private ChessPosition toHighlight=null;
    private int gameID = -1;

    private ChessGame.TeamColor color;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        auth=new AuthData(null, null);
        server = new ServerFacade(serverUrl);
        notificationHandler = new NotificationHandler() {
            public void notify(ServerMessage message) { }
        };
    }
    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverUrl = serverUrl;
        auth=new AuthData(null, null);
        server = new ServerFacade(serverUrl);
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) throws ResponseException {
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
            case "forfeit" -> {
                result = forfeitEval(input);
            }
            case "watch" -> {
                result = watchEval(input);
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
                case "watch", "w" -> watch(params);
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
        throw new ResponseException(400, "Expected: <GAME NAME>");
    }

    public String join(String... params) throws ResponseException{
        if (params.length >= 2) {
            try {
                if (params[1].equalsIgnoreCase("black")) {
                    gameID = server.joinGame(new JoinGameRequest(ChessGame.TeamColor.BLACK, Integer.parseInt(params[0])));
                    color = ChessGame.TeamColor.BLACK;
                } else if (params[1].equalsIgnoreCase("white")) {
                    gameID = server.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, Integer.parseInt(params[0])));
                    color = ChessGame.TeamColor.WHITE;

                } else {
                    throw new ResponseException(400, "Expected: <GAME NUMBER> <COLOR>");
                }
                ws = new WebSocketFacade(serverUrl, notificationHandler);
                ws.joinChessGame(auth.authToken(), gameID);
            }catch(NumberFormatException ex){
                throw new ResponseException(400, "Expected: <GAME NUMBER> <COLOR>");
            }

            stage = "game";
            return "Joined: '"+params[0]+"'";
        }
        throw new ResponseException(400, "Expected: <GAME NUMBER> <COLOR>");
    }
    public String watch(String... params) throws ResponseException{
        if (params.length >= 1) {
            try {
                gameID = server.watchGame(Integer.parseInt(params[0]));
                ws = new WebSocketFacade(serverUrl, notificationHandler);
                ws.joinChessGame(auth.authToken(), gameID);
            }catch(NumberFormatException ex){
                throw new ResponseException(400, "Expected: <GAME NUMBER>");
            }
            stage = "watch";
            return "Joined: '" + params[0] + "'";
        }
        throw new ResponseException(400, "Expected: <GAME NUMBER>");

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
            - Join a game: "j", "join" <GAME NUMBER> <COLOR>
            - Watch a game: "w", "watch" <GAME NUMBER>
            - Logout: "logout"
            - Help: "h", "help"
            """;
    }



    public String printBoard() throws ResponseException {
        ws.getBoard(auth.authToken(), gameID);
        return "";
    }
    public String printBoard(ChessBoard board) throws ResponseException {

        if(toHighlight!=null){
            ChessGame game = new ChessGame(board);
            return printHighlighted(game);
        }

        String[][] toPrint = new String[10][10];

        toPrint[0][0]= SET_BG_COLOR_GREY + SET_TEXT_COLOR_BLACK + EMPTY;
        toPrint[0][1]= " A\u2003";
        toPrint[0][2]= " B\u2003";
        toPrint[0][3]= " C\u2003";
        toPrint[0][4]= " D\u2003";
        toPrint[0][5]= " E\u2003";
        toPrint[0][6]= " F\u2003";
        toPrint[0][7]= " G\u2003";
        toPrint[0][8]= " H\u2003";
        toPrint[0][9]= SET_BG_COLOR_GREY + SET_TEXT_COLOR_BLACK + EMPTY;

        toPrint[9]=toPrint[0];
        boolean darkTile=true;

        for(int i = 1; i<9; i++){
            String[] line = new String[10];
            line[0]=SET_BG_COLOR_GREY + SET_TEXT_COLOR_BLACK + " " + i + "\u2003";
            for(int j = 1; j<9; j++){
                if(darkTile){
                    line[j]=SET_BG_COLOR_DARK_GREY;
                    darkTile=false;
                }else{
                    darkTile=true;
                    line[j]=SET_BG_COLOR_LIGHT_GREY;
                }


                ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                if(piece!=null) {
                    if(piece.getTeamColor()== ChessGame.TeamColor.WHITE) {
                        line[j] += SET_TEXT_COLOR_WHITE;
                        printPieces(line, j, piece, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_PAWN);
                    }
                    else {
                        line[j] += SET_TEXT_COLOR_BLACK;
                        printPieces(line, j, piece, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_PAWN);
                    }
                }
                else{
                    line[j] += EMPTY;
                }
            }
            if(darkTile){
                darkTile=false;
            }else{
                darkTile=true;
            }
            line[9]=SET_BG_COLOR_GREY + SET_TEXT_COLOR_BLACK + " " + i + "\u2003";
            toPrint[i]=line;
        }
        StringBuilder output = printDirection(color, toPrint);
        output.append(RESET_BG_COLOR);

        return output.toString();

    }
    public String printHighlightedBoard(String... params) throws ResponseException {
        toHighlight = new ChessPosition(params[0].charAt(1)-48, params[0].charAt(0)-96);

        return printBoard();
    }
    public String printHighlighted(ChessGame game) throws ResponseException {

        ChessBoard board = game.getBoard();
        ArrayList<ChessPosition> highlighted=new ArrayList<>();
        for(ChessMove move:game.validMoves(toHighlight)){
            highlighted.add(move.getEndPosition());
        }


        String[][] toPrint = new String[10][10];

        toPrint[0][0]= SET_BG_COLOR_GREY + SET_TEXT_COLOR_BLACK + EMPTY;
        toPrint[0][1]= " A\u2003";
        toPrint[0][2]= " B\u2003";
        toPrint[0][3]= " C\u2003";
        toPrint[0][4]= " D\u2003";
        toPrint[0][5]= " E\u2003";
        toPrint[0][6]= " F\u2003";
        toPrint[0][7]= " G\u2003";
        toPrint[0][8]= " H\u2003";
        toPrint[0][9]= SET_BG_COLOR_GREY + SET_TEXT_COLOR_BLACK + EMPTY;

        toPrint[9]=toPrint[0];
        boolean darkTile=true;

        for(int i = 1; i<9; i++){
            String[] line = new String[10];
            line[0]=SET_BG_COLOR_GREY + SET_TEXT_COLOR_BLACK + " " + i + "\u2003";
            for(int j = 1; j<9; j++){
                ChessPosition printSquare = new ChessPosition(i,j);
                if(darkTile){
                    if(highlighted.contains(printSquare)){
                        line[j]=SET_BG_COLOR_DARK_GREEN;
                        darkTile=false;
                    }else{
                        line[j]=SET_BG_COLOR_DARK_GREY;
                        darkTile=false;
                    }
                }else{
                    if(highlighted.contains(printSquare)) {
                        darkTile = true;
                        line[j] = SET_BG_COLOR_GREEN;
                    } else{
                        line[j]=SET_BG_COLOR_LIGHT_GREY;
                        darkTile=true;
                    }
                }

                ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                if(piece!=null) {
                    if(piece.getTeamColor()== ChessGame.TeamColor.WHITE) {
                        line[j] += SET_TEXT_COLOR_WHITE;
                        printPieces(line, j, piece, WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_PAWN);
                    }
                    else {
                        line[j] += SET_TEXT_COLOR_BLACK;
                        printPieces(line, j, piece, BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_PAWN);
                    }
                }
                else{
                    line[j] += EMPTY;
                }
            }
            if(darkTile){
                darkTile=false;
            }else{
                darkTile=true;
            }
            line[9]=SET_BG_COLOR_GREY + SET_TEXT_COLOR_BLACK + " " + i + "\u2003";
            toPrint[i]=line;
        }
        StringBuilder output = printDirection(color, toPrint);
        output.append(RESET_BG_COLOR);
        toHighlight=null;
        return output.toString();

    }

    private void printPieces(String[] line, int j, ChessPiece piece, String r, String kn, String b, String q, String ki, String p) {

        switch (piece.getPieceType()) {
            case ROOK -> line[j] += r;
            case KNIGHT -> line[j] += kn;
            case BISHOP -> line[j] += b;
            case QUEEN -> line[j] += q;
            case KING -> line[j] += ki;
            case PAWN -> line[j] += p;

        }
    }

    private static StringBuilder printDirection(ChessGame.TeamColor color, String[][] toPrint) {
        StringBuilder output= new StringBuilder();

        for(String[] rows: toPrint){
            StringBuilder row= new StringBuilder();
            for(String space: rows){
                if(color == ChessGame.TeamColor.BLACK) {
                    row.insert(0, space);
                }else {
                    row.append(space);
                }
            }
            if(color == ChessGame.TeamColor.BLACK) {
                output.append(row).append(RESET_BG_COLOR).append("\n");
            }else {
                output.insert(0, row + RESET_BG_COLOR + "\n");
            }
        }
        return output;
    }

    public String leave() throws ResponseException{

        ws.leaveChessGame(auth.authToken(), gameID);
        color = null;

        stage="postLogin";
        return "Left game";
    }
    public String makeMove(String... params) throws ResponseException{
        if (params.length<2 || params[0].length()<2 || params[1].length()<2){
            throw new ResponseException(400, "Expected <pos1> <pos2> (ex: a1 b2");
        }

        ChessPosition start = new ChessPosition(params[0].charAt(1)-48, params[0].charAt(0)-96);
        ChessPosition end = new ChessPosition(params[1].charAt(1)-48, params[1].charAt(0)-96);
        ChessMove move=new ChessMove(start, end, null);

        ws.makeMove(auth.authToken(), gameID, move);



        return "";
    }


    public String resign() throws ResponseException{
        stage="forfeit";
        return "Do you really want to quit?";
    }

    public String gameHelp() {
        return """
            Options:
            - Redraw Board: "b", "board"
            - Make Move: "m" , "move" <pos1> <pos2>
            - High Moves: "hi" , "highlight" <pos1>
            - Leave Game: "l", "leave"
            - Resign: "r", "resign"
            - Help: "h", "help"
            """;
    }
    public String gameEval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "board", "b" -> printBoard();
                case "move", "m" -> makeMove(params);
                case "highlight", "hi" -> printHighlightedBoard(params);
                case "leave", "l" -> leave();
                case "resign", "r" -> resign();
                case "help", "h" -> gameHelp();
                default -> gameHelp();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    public String watchHelp() {
        return """
            Options:
            - Redraw Board: "b", "board"
            - Leave Game: "l", "leave"
            - Help: "h", "help"
            """;
    }
    public String watchEval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd.toLowerCase()) {
                case "board", "b" -> printBoard();
                case "highlight", "hi" -> printHighlightedBoard(params);
                case "leave", "l" -> leave();
                case "help", "h" -> watchHelp();
                default -> watchHelp();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    public String forfeitHelp() {
        return """
            Options:
            - Redraw Board: "b", "board"
            - High Moves: "hi" , "highlight" <pos1>
            - Leave Game: "l", "leave"
            - Help: "h", "help"
            """;
    }
    public String forfeitEval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd.toLowerCase()) {
            case "yes", "y" -> forfeit();
            case "help", "h" -> forfeitHelp();
            default -> continueGame();
        };
    }
    public String forfeit() throws ResponseException {
        //forfeit
        ws.resignChessGame(auth.authToken(), gameID);
        stage="watch";
        return "";
    }
    public String continueGame() {
        //forfeit
        stage="game";
        return "Match Continued";
    }

}

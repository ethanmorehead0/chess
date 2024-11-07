package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import model.*;
import spark.*;
import exception.ResponseException;
import service.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class Server {


    private ArrayList<UserData> users = new ArrayList<>();
    private ArrayList<GameData> games = new ArrayList<>();
    private ArrayList<AuthData> Authorization = new ArrayList<>();

    private final ChessService service = new ChessService(new MemoryDataAccess());



    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //try {
        Spark.get("/error", this::Error);
        Spark.exception(ResponseException.class, this::exceptionHandler);
        Spark.notFound((req, res) -> {
            var msg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
            return errorHandler(new Exception(msg), req, res);
        });

        Spark.post("/user", this::Registration);
        Spark.post("/session", this::Login);
        Spark.delete("/session", this::Logout);
        Spark.get("/game", this::ListGames);
        Spark.post("/game", this::JoinGame);
        Spark.put("/game", this::CreateGame);
        Spark.delete("/db", this::Clear);


        /*}catch(ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            System.err.println("Specify the port number as a command line parameter");
            return 404;
        }*/

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public int port() {
        return Spark.port();
    }



    private Object exceptionHandler(ResponseException ex, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", ex.getMessage()), "success", false));
        res.type("application/json");
        System.out.println(ex.StatusCode());
        res.status(ex.StatusCode());
        res.body(body);
        return body;
    }
    private Object throwError(Request req, Response res) {
        throw new RuntimeException("Server on fire");
    }

    public Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }
    private Object Error(Request req, Response res) {
        res.status(404);
        return true;
    }





    private Object Registration(Request req, Response res) throws ResponseException {
        var serializer = new Gson();
        UserData userData = serializer.fromJson(req.body(), UserData.class);
        AuthData auth = service.register(userData);

        return serializer.toJson(auth);
    }

    private Object Login(Request req, Response res) throws ResponseException{
        var serializer = new Gson();
        LoginRequest loginRequest = serializer.fromJson(req.body(), LoginRequest.class);

        AuthData auth = service.Login(loginRequest);

        return serializer.toJson(auth);
    }

    private Object Logout(Request req, Response res) throws ResponseException {
        String auth = req.headers("Authorization");

        service.Logout(auth);

        return "";
    }

    private Object ListGames(Request req, Response res)throws ResponseException {
        //res.type("application/json");
        return new Gson().toJson(Map.of("game", games));
    }

    private Object JoinGame(Request req, Response res)throws ResponseException {

        res.type("application/json");
        return "";
    }

    private Object CreateGame(Request req, Response res)throws ResponseException {
        String auth = req.headers("Authorization");
        var serializer = new Gson();
        /*
        CreateGameRequest GameName = serializer.fromJson(req.body(), CreateGameRequest.class);
        System.out.println(GameName);
        //String gameID = service.CreateGame(GameID);
        //CreateGameResult GameID = service.CreateGame(auth, GameName);*/
        CreateGameResult GameID=new CreateGameResult(15);
        res.body(serializer.toJson(GameID));

        System.out.println(serializer.toJson(GameID));
        System.out.println("hello");
        return serializer.toJson(GameID);
    }

    private Object Clear(Request req, Response res) throws ResponseException {
        service.Clear();
        return "";
    }



    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

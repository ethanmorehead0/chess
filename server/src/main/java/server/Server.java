package server;

import com.google.gson.Gson;
import dataaccess.MySqlDataAccess;
import model.*;
import spark.*;
import exception.ResponseException;
import service.*;

import java.util.*;

public class Server {

    private final ChessService service;

    public Server(){
        try{
            service=new ChessService(new MySqlDataAccess());
        }catch(ResponseException ex){
            throw new RuntimeException(ex);
        }
    }
    public Server(ChessService service) {
        this.service=service;
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.get("/error", this::error);
        Spark.exception(ResponseException.class, this::exceptionHandler);
        Spark.notFound((req, res) -> {
            var msg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
            return errorHandler(new Exception(msg), req, res);
        });

        Spark.post("/user", this::registration);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);
        Spark.post("/game", this::createGame);
        Spark.delete("/db", this::clear);

        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }


    public int port() {
        return Spark.port();
    }



    private Object exceptionHandler(ResponseException ex, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", ex.getMessage()), "success", false));
        res.type("application/json");
        res.status(ex.statusCode());
        res.body(body);
        return body;
    }

    public Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format(e.getMessage()), "success", false));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }
    private Object error(Request req, Response res) {
        res.status(404);
        return true;
    }





    private Object registration(Request req, Response res) throws ResponseException {
        var serializer = new Gson();
        UserData userData = serializer.fromJson(req.body(), UserData.class);
        AuthData auth = service.register(userData);

        return serializer.toJson(auth);
    }

    private Object login(Request req, Response res) throws ResponseException{
        var serializer = new Gson();
        LoginRequest loginRequest = serializer.fromJson(req.body(), LoginRequest.class);
        AuthData auth = service.login(loginRequest);
        return serializer.toJson(auth);
    }

    private Object logout(Request req, Response res) throws ResponseException {
        String auth = req.headers("Authorization");

        service.logout(auth);

        return "";
    }

    private Object listGames(Request req, Response res)throws ResponseException {
        var serializer=new Gson();
        String auth = req.headers("Authorization");
        AllGamesData allGames= service.listGames(auth);

        return serializer.toJson(allGames);
    }

    private Object joinGame(Request req, Response res)throws ResponseException {
        String auth = req.headers("Authorization");
        var serializer = new Gson();
        JoinGameRequest joinGame= serializer.fromJson(req.body(), JoinGameRequest.class);

        service.joinGame(auth, joinGame);


        return "";
    }

    private Object createGame(Request req, Response res)throws ResponseException {
        var serializer = new Gson();
        String auth = req.headers("Authorization");
        CreateGameRequest gameRequest = serializer.fromJson(req.body(), CreateGameRequest.class);


        return serializer.toJson(service.createGame(auth,gameRequest.gameName()));
    }

    private Object clear(Request req, Response res) throws ResponseException {
        service.clear();
        return "";
    }





    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

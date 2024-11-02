package server;

import chess.ChessGame;
import com.google.gson.Gson;
import spark.*;
import exception.ResponseException;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.Map;

public class Server {

    private ArrayList<String> users = new ArrayList<>();
    private ArrayList<ChessGame> games = new ArrayList<>();

    //public Server(Server service) {
    //    this.service = service;
    //}

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //try {

            Spark.post("/user", this::Registration);
            Spark.post("/session", this::Login);
            Spark.delete("/session", this::Logout);
            Spark.get("/game", this::ListGames);
            Spark.post("/game", this::JoinGame);
            Spark.put("/game", this::CreateGame);
            Spark.delete("/db", this::Clear);

            Spark.get("/", this::Error);
            Spark.exception(ResponseException.class, this::exceptionHandler);

        /*}catch(ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            System.err.println("Specify the port number as a command line parameter");
            return 404;
        }*/

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }


    public int port() {
        return Spark.port();
    }



    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
    }

    private Object Registration(Request req, Response res) {
        res.type("application/json");
        /*var pet = new Gson().fromJson(req.body(), Pet.class);
        pet = service.addPet(pet);
        webSocketHandler.makeNoise(user.name(), user.sound());
        return new Gson().toJson(user);*/


        //res.status(404);
        return new Gson().toJson(Map.of("", users));
    }

    private Object Login(Request req, Response res) {
        res.type("application/json");

        return new Gson().toJson(Map.of("user", users));
        //return users;
    }

    private Object Logout(Request req, Response res) {
        users.remove(req.params(":user"));
        return "";
    }

    private Object ListGames(Request req, Response res) {
        res.type("application/json");
        return new Gson().toJson(Map.of("user", users));
    }

    private Object JoinGame(Request req, Response res) {

        res.type("application/json");
        return new Gson().toJson(Map.of("user", users));
    }

    private Object CreateGame(Request req, Response res) {
        res.type("application/json");
        return new Gson().toJson(Map.of("user", users));
    }

    private Object Clear(Request req, Response res) {
        users.remove(req.params(":user"));
        //res.status(404);
        return "";
    }


    private Object Error(Request req, Response res) {
        //res.status(404);
        return true;
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

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

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public int port() {
        return Spark.port();
    }



    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
    }

    private Object Registration(Request req, Response res) throws ResponseException {
        res.type("application/json");
        /*var pet = new Gson().fromJson(req.body(), Pet.class);
        pet = service.addPet(pet);
        webSocketHandler.makeNoise(user.name(), user.sound());
        return new Gson().toJson(user);*/


        //res.status(404);
        return new Gson().toJson(Map.of("", users));
    }

    private Object Login(Request req, Response res) throws ResponseException{
        var serializer = new Gson();
        LoginRequest loginRequest = serializer.fromJson(req.body(), LoginRequest.class);

        AuthData auth = service.Login(loginRequest);

        return serializer.toJson(auth);
    }

    private Object Logout(Request req, Response res) throws ResponseException {
        var serializer = new Gson();
        LogoutRequest logoutRequest = serializer.fromJson(req.body(), LogoutRequest.class);

        service.Logout(logoutRequest);

        return "";
    }

    private Object ListGames(Request req, Response res)throws ResponseException {
        res.type("application/json");
        return new Gson().toJson(Map.of("game", games));
    }

    private Object JoinGame(Request req, Response res)throws ResponseException {

        res.type("application/json");
        return "";
    }

    private Object CreateGame(Request req, Response res)throws ResponseException {
        res.type("application/json");
        return new Gson().toJson(Map.of("user", users));
    }

    private Object Clear(Request req, Response res) throws ResponseException {
        service.Clear();
        //res.status(200);

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

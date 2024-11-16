package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerFacade {

    private final String serverUrl;
    private AuthData auth = new AuthData(null, null);
    private AllGamesData lastListedGameSet = new AllGamesData(new ArrayList<>());

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData registration(UserData data) throws ResponseException {
        var path = "/user";
        try {
            auth = this.makeRequest("POST", path, data, AuthData.class);
        }catch (ResponseException ex){
            throw new ResponseException(400, "Username already exists");
        }

        return auth;
    }

    public AuthData login(LoginRequest req) throws ResponseException {
        var path = "/session";
        try {
            auth = this.makeRequest("POST", path, req, AuthData.class);
        }catch(ResponseException ex){
            throw new ResponseException(400, "Invalid user/password");
        }
        return auth;
    }

    public void logout() throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
        auth = new AuthData(null, null);
    }

    public AllGamesData listGames() throws ResponseException {
        var path = "/game";
        lastListedGameSet = this.makeRequest("GET", path, null, AllGamesData.class);

        return lastListedGameSet;
    }

    public int joinGame(JoinGameRequest req) throws ResponseException {
        if(req.gameID()>lastListedGameSet.games().size() || req.gameID()<=0){
            throw new ResponseException(401,"Invalid Game ID");
        }
        try {
            int gameID = lastListedGameSet.games().get(req.gameID() - 1).gameID();
            req = new JoinGameRequest(req.playerColor(), gameID);
            var path = "/game";
            this.makeRequest("PUT", path, req, null);
            return gameID;
        }catch(ResponseException ex){
            throw new ResponseException(400,"game already full");
        }

    }
    public int watchGame(int gameNumber) throws ResponseException {
        if(gameNumber>lastListedGameSet.games().size() || gameNumber<=0){
            throw new ResponseException(401,"Invalid Game ID");
        }
        return lastListedGameSet.games().get(gameNumber-1).gameID();
    }

    public CreateGameResult createGame(CreateGameRequest req) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, req, CreateGameResult.class);
    }


    public void clear() throws ResponseException {
        var path = "/db";
        auth = new AuthData(null, null);
        lastListedGameSet =  new AllGamesData(new ArrayList<>());
        this.makeRequest("DELETE", path, null, null);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            http.setRequestProperty("Authorization", auth.authToken());

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }



}

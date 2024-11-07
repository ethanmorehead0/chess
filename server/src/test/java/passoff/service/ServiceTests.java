package passoff.service;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import passoff.model.*;
import service.ChessService;
import dataaccess.MemoryDataAccess;
import model.*;

import dataaccess.DataAccessException;

import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ChessService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {
    private static ChessService service;
    private UserData defaultUser=new UserData("TestName","TestPassword","TestEmail@test.com");
    private AuthData defaultUserAuth;
    private CreateGameResult defaultGame;

    @BeforeAll
    public static void init() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        service = new ChessService(dataAccess);
    }

    @BeforeEach
    public void setup() throws ResponseException {
        service.Clear();

        //one user already logged in
        defaultUserAuth = service.register(defaultUser);
        defaultGame = service.CreateGame(defaultUserAuth.authToken(),"defaultGame" );
    }

    @Test
    @Order(1)
    @DisplayName("Clear")
    public void clear() throws ResponseException {
        service.Clear();
        Assertions.assertThrows(ResponseException.class, () -> service.Login(new LoginRequest(defaultUser.username(),defaultUser.password())));
        defaultUserAuth = service.register(new UserData("TestName","TestPassword","TestEmail@test.com"));
        var games=service.ListGames(defaultUserAuth.authToken());
        Assertions.assertTrue(games.isEmpty());
    }
    @Test
    @Order(2)
    @DisplayName("Multiple Clears")
    public void multipleClears() throws ResponseException{
        UserData user = new UserData("user","password","user@test.com");
        AuthData auth= service.register(user);
        service.CreateGame(auth.authToken(),"gameName");

        service.Clear();
        Assertions.assertThrows(ResponseException.class, () -> service.Login(new LoginRequest(defaultUser.username(),defaultUser.password())));
        defaultUserAuth = service.register(new UserData("TestName","TestPassword","TestEmail@test.com"));

        var games=service.ListGames(defaultUserAuth.authToken());
        Assertions.assertTrue(games.isEmpty());

        Assertions.assertThrows(ResponseException.class, () -> service.Login(new LoginRequest(user.username(),user.password())));
        auth = service.register(new UserData("user","password","user@test.com"));
        games=service.ListGames(auth.authToken());
        Assertions.assertTrue(games.isEmpty());

    }

    @Test
    @Order(3)
    @DisplayName("Register")
    public void register() throws ResponseException {
        UserData newUser= new UserData("newUser","newPassword", "newUser@new.com");

        AuthData ResultAuthData = service.register(newUser);
        Assertions.assertEquals(newUser.username(),ResultAuthData.username(),"Response username does not equal registered username");
        Assertions.assertNotNull(ResultAuthData.authToken(), "Response did not contain an authentication string");
    }
    @Test
    @Order(4)
    @DisplayName("Re-Register User")
    public void registerTwice() {
        assertThrows(ResponseException.class, () -> service.register(defaultUser));
    }

    @Test
    @Order(5)
    @DisplayName("Register Bad Request")
    public void registerBadRequest() {
        UserData newUser= new UserData("newUser","newPassword", null);
        assertThrows(ResponseException.class, () -> service.register(newUser));
    }

    @Test
    @Order(6)
    @DisplayName("Login")
    public void login() throws ResponseException{
        LoginRequest req= new LoginRequest(defaultUser.username(),defaultUser.password());
        AuthData ResultAuthData = service.Login(req);
        Assertions.assertEquals(defaultUser.username(),ResultAuthData.username(),"Response username does not equal registered username");
        Assertions.assertNotNull(ResultAuthData.authToken(), "Response did not contain an authentication string");

    }

    @Test
    @Order(7)
    @DisplayName("Login Wrong Password")
    public void wrongPassword() throws ResponseException{
        LoginRequest req= new LoginRequest(defaultUser.username(),"incorrect password");
        assertThrows(ResponseException.class, () -> service.Login(req));
    }

    @Test
    @Order(8)
    @DisplayName("Login Invalid User")
    public void loginInvalidUser() {
        LoginRequest req= new LoginRequest("invalidUser",defaultUser.password());
        assertThrows(ResponseException.class, () -> service.Login(req));

    }

    @Test
    @Order(9)
    @DisplayName("Logout")
    public void logout() throws ResponseException{
        Assertions.assertDoesNotThrow(() -> service.Logout(defaultUserAuth.authToken()), "throws exception");
    }

    @Test
    @Order(10)
    @DisplayName("Invalid Auth Logout")
    public void invalidAuthLogout() {
        Assertions.assertThrows(ResponseException.class,() ->service.Logout("Invalid AuthToken"));
    }

    @Test
    @Order(11)
    @DisplayName("List Games")
    public void listGames() throws ResponseException{
        var games = service.ListGames(defaultUserAuth.authToken());
        Assertions.assertNotNull(games);
    }

    @Test
    @Order(12)
    @DisplayName("List Games Invalid Auth")
    public void ListGamesEmpty() throws ResponseException {

        Assertions.assertThrows(ResponseException.class,() -> service.ListGames("InvalidAuth"), "Invalid Auth Token");

    }

    @Test
    @Order(13)
    @DisplayName("Create Game")
    public void createGame() throws ResponseException{
        UserData user=new UserData("name","password","email@test.com");
        AuthData userAuth=service.register(user);

        CreateGameResult CreateGameID = service.CreateGame(userAuth.authToken(),"NewGame");
        Assertions.assertNotNull(CreateGameID);
        Assertions.assertNotNull(service.ListGames(userAuth.authToken()));
    }

    @Test
    @Order(14)
    @DisplayName("Invalid Create Game Request")
    public void InvalidCreateGame() throws ResponseException{
        Assertions.assertThrows(ResponseException.class, () -> service.CreateGame("Invalid Auth", "Game"));
    }

    @Test
    @Order(15)
    @DisplayName("Join Game")
    public void joinGame() throws ResponseException{
        UserData newUser= new UserData("newUser","newPassword", "newUser@new.com");
        AuthData ResultAuthData = service.register(newUser);
        service.JoinGame(ResultAuthData.authToken(), new JoinGameRequest(ChessGame.TeamColor.BLACK,defaultGame.gameID()));
        Assertions.assertNotNull(service.ListGames(ResultAuthData.authToken()));
    }
    @Test
    @Order(16)
    @DisplayName("Join Game - no game")
    public void joinNoGame() throws ResponseException{
        UserData newUser= new UserData("newUser","newPassword", "newUser@new.com");
        AuthData ResultAuthData = service.register(newUser);
        Assertions.assertThrows(ResponseException.class, () -> service.JoinGame(ResultAuthData.authToken(), new JoinGameRequest(ChessGame.TeamColor.BLACK,-1)));
    }
    @Test
    @Order(17)
    @DisplayName("Join Game - same user")
    public void joinOwnGame() {
        Assertions.assertThrows(ResponseException.class, () -> service.JoinGame(defaultUserAuth.authToken(), new JoinGameRequest(ChessGame.TeamColor.BLACK,-1)));
    }

    @Test
    @Order(18)
    @DisplayName("Join Game - spot taken")
    public void spotTaken() throws ResponseException{
        UserData newUser= new UserData("newUser","newPassword", "newUser@new.com");
        AuthData ResultAuthData = service.register(newUser);
        service.JoinGame(ResultAuthData.authToken(), new JoinGameRequest(ChessGame.TeamColor.BLACK,defaultGame.gameID()));

        UserData newUser1= new UserData("newUser1","newPassword1", "newUser1@new.com");
        AuthData ResultAuthData1 = service.register(newUser1);
        service.JoinGame(ResultAuthData1.authToken(), new JoinGameRequest(ChessGame.TeamColor.BLACK,defaultGame.gameID()));
    }

}


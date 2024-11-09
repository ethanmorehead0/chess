import chess.ChessGame;
import org.junit.jupiter.api.*;
import service.ChessService;
import dataaccess.MemoryDataAccess;
import model.*;

import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Service {
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
        service.clear();

        //one user already logged in
        defaultUserAuth = service.register(defaultUser);
        defaultGame = service.createGame(defaultUserAuth.authToken(),"defaultGame" );
    }

    @Test
    @Order(1)
    @DisplayName("Clear")
    public void clear() throws ResponseException {
        service.clear();
        Assertions.assertThrows(ResponseException.class, () -> service.login(new LoginRequest(defaultUser.username(),defaultUser.password())));
        defaultUserAuth = service.register(new UserData("TestName","TestPassword","TestEmail@test.com"));
        var games=service.listGames(defaultUserAuth.authToken());
        Assertions.assertTrue(games.isEmpty());
    }
    @Test
    @Order(2)
    @DisplayName("Multiple Clears")
    public void multipleClears() throws ResponseException{
        UserData user = new UserData("user","password","user@test.com");
        AuthData auth= service.register(user);
        service.createGame(auth.authToken(),"gameName");

        service.clear();
        Assertions.assertThrows(ResponseException.class, () -> service.login(new LoginRequest(defaultUser.username(),defaultUser.password())));
        defaultUserAuth = service.register(new UserData("TestName","TestPassword","TestEmail@test.com"));

        var games=service.listGames(defaultUserAuth.authToken());
        Assertions.assertTrue(games.isEmpty());

        Assertions.assertThrows(ResponseException.class, () -> service.login(new LoginRequest(user.username(),user.password())));
        auth = service.register(new UserData("user","password","user@test.com"));
        games=service.listGames(auth.authToken());
        Assertions.assertTrue(games.isEmpty());

    }

    @Test
    @Order(3)
    @DisplayName("Register")
    public void register() throws ResponseException {
        UserData newUser= new UserData("newUser","newPassword", "newUser@new.com");

        AuthData resultAuthData = service.register(newUser);
        Assertions.assertEquals(newUser.username(),resultAuthData.username(),"Response username does not equal registered username");
        Assertions.assertNotNull(resultAuthData.authToken(), "Response did not contain an authentication string");
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
        AuthData resultAuthData = service.login(req);
        Assertions.assertEquals(defaultUser.username(),resultAuthData.username(),"Response username does not equal registered username");
        Assertions.assertNotNull(resultAuthData.authToken(), "Response did not contain an authentication string");

    }

    @Test
    @Order(7)
    @DisplayName("Login Wrong Password")
    public void wrongPassword() throws ResponseException{
        LoginRequest req= new LoginRequest(defaultUser.username(),"incorrect password");
        assertThrows(ResponseException.class, () -> service.login(req));
    }

    @Test
    @Order(8)
    @DisplayName("Login Invalid User")
    public void loginInvalidUser() {
        LoginRequest req= new LoginRequest("invalidUser",defaultUser.password());
        assertThrows(ResponseException.class, () -> service.login(req));

    }

    @Test
    @Order(9)
    @DisplayName("Logout")
    public void logout() throws ResponseException{
        Assertions.assertDoesNotThrow(() -> service.logout(defaultUserAuth.authToken()), "throws exception");
    }

    @Test
    @Order(10)
    @DisplayName("Invalid Auth Logout")
    public void invalidAuthLogout() {
        Assertions.assertThrows(ResponseException.class,() ->service.logout("Invalid AuthToken"));
    }

    @Test
    @Order(11)
    @DisplayName("List Games")
    public void listGames() throws ResponseException{
        var games = service.listGames(defaultUserAuth.authToken());
        Assertions.assertNotNull(games);
    }

    @Test
    @Order(12)
    @DisplayName("List Games Invalid Auth")
    public void listGamesEmpty() throws ResponseException {

        Assertions.assertThrows(ResponseException.class,() -> service.listGames("InvalidAuth"), "Invalid Auth Token");

    }

    @Test
    @Order(13)
    @DisplayName("Create Game")
    public void createGame() throws ResponseException{
        UserData user=new UserData("name","password","email@test.com");
        AuthData userAuth=service.register(user);

        CreateGameResult createGameID = service.createGame(userAuth.authToken(),"NewGame");
        Assertions.assertNotNull(createGameID);
        Assertions.assertNotNull(service.listGames(userAuth.authToken()));
    }

    @Test
    @Order(14)
    @DisplayName("Invalid Create Game Request")
    public void invalidCreateGame() throws ResponseException{
        Assertions.assertThrows(ResponseException.class, () -> service.createGame("Invalid Auth", "Game"));
    }

    @Test
    @Order(15)
    @DisplayName("Join Game")
    public void joinGame() throws ResponseException{
        UserData newUser= new UserData("newUser","newPassword", "newUser@new.com");
        AuthData resultAuthData = service.register(newUser);
        service.joinGame(resultAuthData.authToken(), new JoinGameRequest(ChessGame.TeamColor.BLACK,defaultGame.gameID()));
        Assertions.assertNotNull(service.listGames(resultAuthData.authToken()));
    }
    @Test
    @Order(16)
    @DisplayName("Join Game - no game")
    public void joinNoGame() throws ResponseException{
        UserData newUser= new UserData("newUser","newPassword", "newUser@new.com");
        AuthData resultAuthData = service.register(newUser);
        JoinGameRequest join = new JoinGameRequest(ChessGame.TeamColor.BLACK,-1);
        Assertions.assertThrows(ResponseException.class, () -> service.joinGame(resultAuthData.authToken(), join));
    }
    @Test
    @Order(17)
    @DisplayName("Join Game - same user")
    public void joinOwnGame() {
        JoinGameRequest join = new JoinGameRequest(ChessGame.TeamColor.BLACK,-1);
        Assertions.assertThrows(ResponseException.class, () -> service.joinGame(defaultUserAuth.authToken(), join));
    }

    @Test
    @Order(18)
    @DisplayName("Join Game - spot taken")
    public void spotTaken() throws ResponseException{
        UserData newUser= new UserData("newUser","newPassword", "newUser@new.com");
        AuthData resultAuthData = service.register(newUser);
        service.joinGame(resultAuthData.authToken(), new JoinGameRequest(ChessGame.TeamColor.BLACK,defaultGame.gameID()));

        UserData newUser1= new UserData("newUser1","newPassword1", "newUser1@new.com");
        AuthData resultAuthData1 = service.register(newUser1);
        JoinGameRequest join = new JoinGameRequest(ChessGame.TeamColor.BLACK,defaultGame.gameID());
        Assertions.assertThrows(ResponseException.class, () -> service.joinGame(resultAuthData1.authToken(), join));
    }

}


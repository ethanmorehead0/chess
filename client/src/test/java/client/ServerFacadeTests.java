package client;

import chess.ChessGame;
import chess.ChessPiece;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);


        var serverUrl = "http://localhost:"+port;
        serverFacade=new ServerFacade(serverUrl);

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void reset() throws ResponseException {

        serverFacade.clear();
        serverFacade.registration(new UserData("username","password", "email"));
    }


    @Test
    @Order(1)
    @DisplayName("valid Registration")
    public void registrationTest() throws ResponseException {
        Assertions.assertDoesNotThrow(() -> serverFacade.registration(new UserData("registerName","password","email")));

    }
    @Test
    @Order(2)
    @DisplayName("invalid Registration")
    public void invalidRegistrationTest() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, ()->serverFacade.registration(new UserData(null,null,null)));
        Assertions.assertThrows(ResponseException.class, ()->serverFacade.registration(new UserData("username","password","email")));
    }
    @Test
    @Order(3)
    @DisplayName("login")
    public void loginTest() {
        Assertions.assertDoesNotThrow(()-> serverFacade.login(new LoginRequest("username","password")));

    }
    @Test
    @Order(4)
    @DisplayName("invalid user")
    public void invalidUserTest() {
        Assertions.assertThrows(ResponseException.class, ()-> serverFacade.login(new LoginRequest("invalidUser", "password")));

    }

    @Test
    @Order(5)
    @DisplayName("invalid password")
    public void invalidPassword() {
        Assertions.assertThrows(ResponseException.class, ()-> serverFacade.login(new LoginRequest("username", "invalidPassword")));

    }

    @Test
    @Order(6)
    @DisplayName("logout")
    public void logoutTest() {
        Assertions.assertDoesNotThrow(()-> serverFacade.logout());
    }
    @Test
    @Order(7)
    @DisplayName("list games")
    public void listGamesTest() throws ResponseException {
        serverFacade.createGame(new CreateGameRequest("newGame"));
        Assertions.assertDoesNotThrow(()->serverFacade.listGames());

    }
    @Test
    @Order(8)
    @DisplayName("no games list games")
    public void emptyListGamesTest() {
        Assertions.assertDoesNotThrow(()->serverFacade.listGames());
    }
    @Test
    @Order(9)
    @DisplayName("join Game")
    public void joinGameTest() throws ResponseException {
        serverFacade.createGame( new CreateGameRequest("newGame"));
        serverFacade.listGames();
        Assertions.assertDoesNotThrow(()->serverFacade.joinGame(new JoinGameRequest(ChessGame.TeamColor.BLACK,1)));
    }
    @Test
    @Order(10)
    @DisplayName("join game without listing games")
    public void invalidJoinGameTest() throws ResponseException {
        serverFacade.createGame( new CreateGameRequest("newGame"));

        Assertions.assertThrows(ResponseException.class, ()->serverFacade.joinGame(new JoinGameRequest(ChessGame.TeamColor.BLACK,1)));
    }
    @Test
    @Order(11)
    @DisplayName("join invalid game")
    public void joinInvalidGameTest() throws ResponseException {
        serverFacade.createGame( new CreateGameRequest("newGame"));
        serverFacade.listGames();
        Assertions.assertThrows(ResponseException.class, ()->serverFacade.joinGame(new JoinGameRequest(ChessGame.TeamColor.BLACK,77)));
    }
    @Test
    @Order(11)
    @DisplayName("create game")
    public void createGameTest() throws ResponseException {
        Assertions.assertDoesNotThrow(()-> serverFacade.createGame(new CreateGameRequest("newGame")));
        Assertions.assertNotNull(serverFacade.listGames());
    }
    @Test
    @Order(12)
    @DisplayName("repeat game name")
    public void invalidCreateGameTest() throws ResponseException {
        serverFacade.createGame(new CreateGameRequest("newGame"));
        Assertions.assertDoesNotThrow(()-> serverFacade.createGame(new CreateGameRequest("newGame")));
    }

    @Test
    @Order(13)
    @DisplayName("clear")
    public void clear() {
        Assertions.assertDoesNotThrow(()-> serverFacade.clear());
    }

}

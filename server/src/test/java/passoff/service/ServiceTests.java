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

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {
    private static ChessService service;
    private UserData defaultUser=new UserData("TestName","TestPassword","TestEmail@test.com");
    private AuthData defaultUserAuth;

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
    }

    @Test
    @DisplayName("a")
    public void serverTests() {
        assertEquals(200, 100 + 100);
        assertEquals(100, 2 * 50);
        assertNotNull(new Object(), "Response did not return authentication String");
        assertThrows(DataAccessException.class, () -> {
            throw new DataAccessException(404, "hello world");
        });
    }
    @Test
    @Order(1)
    @DisplayName("Clear")
    public void clear() throws ResponseException {

    }
    @Test
    @Order(2)
    @DisplayName("Multiple Clears")
    public void multipleClears() {

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
    public void listGames() {

    }

    @Test
    @Order(12)
    @DisplayName("List Games Empty")
    public void ListGamesEmpty() {

    }

    @Test
    @Order(13)
    @DisplayName("Create Game")
    public void createGame() {

    }

    @Test
    @Order(14)
    @DisplayName("Invalid Create Game Request")
    public void InvalidCreateGame() {

    }

    @Test
    @Order(15)
    @DisplayName("Join Game")
    public void joinGame() {

    }

}


package dataaccess;

import model.*;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{
    private int gameNumber=1;
    private ArrayList<UserData> users = new ArrayList<>();
    private HashMap<Integer, GameData> games = new HashMap<>();
    private ArrayList<AuthData> Authorization = new ArrayList<>();


    public void clear() {
        users.clear();
        games.clear();
        Authorization.clear();
    };

    public void createUser(UserData user) {
        users.add(user);
    };
    public UserData getUser(String username) {
        if(users!=null) {
            for (UserData user : users) {
                if (user.username().equals(username)) {
                    return user;
                }
            }
        }
        return null;
    };
    public int createGame(String username, String gameName) {

        GameData game = new GameData(gameNumber, null, null, gameName);
        games.put(gameNumber, game);
        gameNumber+=1;
        return game.gameID();
    };
    public GameData getGame(int gameID) {
        return games.get(gameID);

    };
    public Collection<GameData> listGames(String auth) {
        System.out.println("1: " + games.values());
        return games.values();
    };

    public void updateGame(String auth, GameData data) {
        //only those that are in game can change
        var game = getGame(data.gameID());
        //game=data;
        games.remove(game);
        games.put(data.gameID(), data);
        //listGames(auth);

    };

    public void createAuth(AuthData auth) {
        Authorization.add(auth);
    };

    public AuthData getAuth(String auth) {
        System.out.print("getAuth: ");
        if(Authorization!=null) {
            for (AuthData authorization : Authorization) {
                if (authorization.authToken().equals(auth)) {
                    return authorization;
                }
            }
        }
        System.out.println(auth + "  --  " + Authorization);
        return null;
        //return new AuthData(null,null);
    };
    public void deleteAuth(AuthData auth) {
        if(Authorization!=null) {
            System.out.println("First " + Authorization);
            Authorization.removeIf(authorization -> authorization.equals(auth));
            System.out.println("After " + Authorization);
        }
    };

}

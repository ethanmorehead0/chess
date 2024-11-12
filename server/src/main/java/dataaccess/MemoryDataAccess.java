package dataaccess;

import model.*;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{
    private int gameNumber=1;
    private ArrayList<UserData> users = new ArrayList<>();
    private HashMap<Integer, GameData> games = new HashMap<>();
    private ArrayList<AuthData> authorization = new ArrayList<>();


    public void clear() {
        users.clear();
        games.clear();
        authorization.clear();
    }

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
    }
    public int createGame(String username, String gameName) {

        GameData game = new GameData(gameNumber, null, null, gameName);
        games.put(gameNumber, game);
        gameNumber+=1;
        return game.gameID();
    }
    public GameData getGame(int gameID) {
        return games.get(gameID);

    }
    public Collection<GameData> listGames(String auth) {
        return games.values();
    }

    public void updateGame(String auth, GameData data) {
        //only those that are in game can change
        var game = getGame(data.gameID());
        //game=data;
        games.remove(game);
        games.put(data.gameID(), data);
        //listGames(auth);

    }

    public void createAuth(AuthData auth) {
        authorization.add(auth);
    };

    public AuthData getAuth(String auth) {
        if(authorization !=null) {
            for (AuthData authorization : authorization) {
                if (authorization.authToken().equals(auth)) {
                    return authorization;
                }
            }
        }
        return null;
        //return new AuthData(null,null);
    }
    public void deleteAuth(AuthData auth) {
        if(authorization !=null) {
            authorization.removeIf(authorization -> authorization.equals(auth));
        }
    }

}

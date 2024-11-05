package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;


import java.util.ArrayList;

public class MemoryDataAccess implements DataAccess{
    private ArrayList<UserData> users = new ArrayList<>();
    private ArrayList<GameData> games = new ArrayList<>();
    private ArrayList<AuthData> Authorization = new ArrayList<>();

    public void clear() {
        users.clear();
        games.clear();
    };

}

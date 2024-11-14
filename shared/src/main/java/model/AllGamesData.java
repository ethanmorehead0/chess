package model;

import java.util.Collection;

public record AllGamesData (Collection<GameData> games) {
    public boolean isEmpty() {
        if (games.isEmpty()){
            return true;
        }
        return false;
    }
}

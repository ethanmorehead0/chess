package model;

import java.util.Collection;

public record AllGamesData (Collection<GameData> games) {
    public boolean isEmpty() {
        if (games.isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder output= new StringBuilder();
        for(GameData game:games){
            output.append(game).append("\n");
        }
        return output.toString();
    }
}

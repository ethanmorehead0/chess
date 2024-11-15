package model;

import java.util.ArrayList;
import java.util.Collection;

public record AllGamesData (ArrayList<GameData> games) {
    public boolean isEmpty() {
        if (games.isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder output= new StringBuilder();
        int gameNumber=1;
        for(GameData game:games){
            output.append(gameNumber).append(". ").append(game).append("\n");
            gameNumber++;
        }
        return output.toString();
    }
}

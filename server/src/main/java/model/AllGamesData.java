package model;

import chess.ChessGame;

import java.util.ArrayList;
import java.util.Collection;

public record AllGamesData (Collection<GameData> Games) {
    public boolean isEmpty() {
        if (Games.isEmpty()){
            return true;
        }
        return false;
    }
}

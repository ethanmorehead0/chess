package model;

import chess.ChessGame;

public class AllGamesData {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame game;

    public AllGamesData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }
    public int getGameData(){
        return 10;
    }
}

package model;

public record GameData (Integer gameID, String whiteUsername, String blackUsername, String gameName) {
    @Override
    public String toString() {
        String white="'" + whiteUsername + "'";
        String black="'" + blackUsername + "'";
        if (white.equals("'null'")){
            white=" - ";
        }
        if(black.equals("'null'")){
            black=" - ";
        }

        return "Game Name: " + gameName + "   White Username: " + white +
                "  Black Username: " + black;
    }
}
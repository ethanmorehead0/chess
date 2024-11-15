package model;

/*public record GameData(Integer integer, String s, String s1, String s2) {
}*/
public record GameData (Integer gameID, String whiteUsername, String blackUsername, String gameName) {
    @Override
    public String toString() {
        return "Game Name: " + gameName + ": White Username - '" + whiteUsername +
                "', Black Username- '" + blackUsername + "'";
    }
}
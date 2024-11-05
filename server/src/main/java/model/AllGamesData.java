package model;

import chess.ChessGame;

public record AllGamesData (int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {}

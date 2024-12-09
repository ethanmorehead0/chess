package model;

import chess.ChessGame;

public record LeaveGameRequest(ChessGame.TeamColor playerColor, Integer gameID) {}

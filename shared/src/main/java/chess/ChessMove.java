package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof ChessMove chessMove)) {return false;}

        return Objects.equals(startPos, chessMove.startPos) && Objects.equals(endPos, chessMove.endPos) && promPiece == chessMove.promPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPos, endPos, promPiece);
    }

    @Override
    public String toString() {
        return "CM: " +
                "start " + startPos +
                ", end " + endPos +
                ", promotion " + promPiece;
    }

    private ChessPosition startPos;
    private ChessPosition endPos;
    private ChessPiece.PieceType promPiece;

    public ChessMove(ChessPosition startPos, ChessPosition endPos,
                     ChessPiece.PieceType promotionPiece) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.promPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPos() {
        return startPos;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPos() {
        return endPos;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromPiece() {
        return promPiece;
    }
}

package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof ChessBoard that)) {return false;}
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "CB" + Arrays.deepToString(squares);
    }

    private ChessPiece [][] squares = new ChessPiece[8][8];
    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }


    public void removePiece(ChessPosition position){
        squares[position.getRow()-1][position.getColumn()-1] = null;
    }
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if(squares[position.getRow()-1][position.getColumn()-1] == null){return null;}
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];
        for(int i=0;i<8;i++){
            squares[1][i]=new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            squares[6][i]=new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        ChessGame.TeamColor team = ChessGame.TeamColor.WHITE;
        for(int i=0;i<8;i+=7){
            squares[i][0]=new ChessPiece(team, ChessPiece.PieceType.ROOK);
            squares[i][7]=new ChessPiece(team, ChessPiece.PieceType.ROOK);

            squares[i][1]=new ChessPiece(team, ChessPiece.PieceType.KNIGHT);
            squares[i][6]=new ChessPiece(team, ChessPiece.PieceType.KNIGHT);

            squares[i][2]=new ChessPiece(team, ChessPiece.PieceType.BISHOP);
            squares[i][5]=new ChessPiece(team, ChessPiece.PieceType.BISHOP);

            squares[i][3]=new ChessPiece(team, ChessPiece.PieceType.QUEEN);
            squares[i][4]=new ChessPiece(team, ChessPiece.PieceType.KING);
            team= ChessGame.TeamColor.BLACK;
        }
    }
}

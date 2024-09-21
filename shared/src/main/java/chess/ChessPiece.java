package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "CP: " + type + " - " + pieceColor;
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves=new ArrayList<ChessMove>();
        switch (board.getPiece(myPosition).getPieceType()){
            case KING:
                int[][] kingMove={{1,0,0},{1,1,0},{0,1,0},{-1,1,0},{-1,0,0},{-1,-1,0},{0,-1,0},{1,-1,0}};
                addMoves(board,myPosition,moves,kingMove);
                break;
            case QUEEN:
                int[][] queenMove={{1,0,1},{1,1,1},{0,1,1},{-1,1,1},{-1,0,1},{-1,-1,1},{0,-1,1},{1,-1,1}};
                addMoves(board,myPosition,moves,queenMove);
                break;
            case BISHOP:
                int[][] bishopMove={{1,1,1},{-1,1,1},{-1,-1,1},{1,-1,1}};
                addMoves(board,myPosition,moves,bishopMove);
                break;
            case KNIGHT:
                int[][] knightMove={{2,1,0},{1,2,0},{-1,2,0},{-2,1,0},{-2,-1,0},{-1,-2,0},{1,-2,0},{2,-1,0}};
                addMoves(board,myPosition,moves,knightMove);
                break;
            case ROOK:
                int[][] rookMove={{1,0,1}, {0,1,1},{-1,0,1},{0,-1,1},{1,-1,1}};
                addMoves(board,myPosition,moves,rookMove);

                break;
            case PAWN:
                int[][] pawnMove={{0,0,0}};
                addMoves(board,myPosition,moves,pawnMove);

                break;
        }

        return moves;
    }

    private void addMoves (ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> moves, int[][]movementType){
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        for (int[] movement:movementType){
            int addRow=movement[0];
            int addColumn=movement[1];
            do{
                ChessPosition newPostion=new ChessPosition(row+movement[0],column+movement[1]);
                if(newPostion.getRow()>0 && newPostion.getRow()<=8 && newPostion.getColumn()>0 && newPostion.getColumn()<=8){
                    if(board.getPiece(newPostion) == null){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row+movement[0],column+movement[1]), null));
                    }
                    else if(board.getPiece(newPostion).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                        moves.add(new ChessMove(myPosition, new ChessPosition(row+movement[0],column+movement[1]), null));
                        movement[2]=0;
                    }
                }
                else{movement[2]=0;}
                movement[0]+=addRow;
                movement[1]+=addColumn;
            }while(movement[2]==1);
        }


        //moves.add(new ChessMove(myPosition, myPosition, null));
    }

}

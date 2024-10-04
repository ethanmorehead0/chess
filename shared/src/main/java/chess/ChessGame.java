package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(move.getPromotionPiece()!=null){
            board.addPiece(move.getEndPosition(), new ChessPiece(board.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece()));
        }
        else{
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        }
            board.addPiece(move.getStartPosition(),null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition pos = new ChessPosition(-1, -1);
        for(int i=1;i<9;i++){
            for(int j=1;j<9;j++){
                if(board.getPiece(new ChessPosition(i,j)) != null && board.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING && board.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor){
                    pos=new ChessPosition(i,j);
                }
            }
        }
        boolean isInCheck=false;

        int[][] queenMove={{1,0,1},{1,1,1},{0,1,1},{-1,1,1},{-1,0,1},{-1,-1,1},{0,-1,1},{1,-1,1}};
        isInCheck=checkPieces(pos,teamColor,queenMove, ChessPiece.PieceType.QUEEN);

        int[][] bishopMove={{1,1,1},{-1,1,1},{-1,-1,1},{1,-1,1}};
        isInCheck=isInCheck || checkPieces(pos,teamColor,bishopMove, ChessPiece.PieceType.BISHOP);

        int[][] knightMove={{2,1,0},{1,2,0},{-1,2,0},{-2,1,0},{-2,-1,0},{-1,-2,0},{1,-2,0},{2,-1,0}};
        isInCheck=isInCheck || checkPieces(pos,teamColor,knightMove, ChessPiece.PieceType.KNIGHT);

        int[][] rookMove={{1,0,1}, {0,1,1},{-1,0,1},{0,-1,1}};
        isInCheck=isInCheck || checkPieces(pos,teamColor,rookMove, ChessPiece.PieceType.ROOK);


        return isInCheck;
    }

    //Checks the pieces on the board to see if any of them can move to the position of the king.
    private boolean checkPieces (ChessPosition kingPosition, TeamColor color, int[][]movementType, ChessPiece.PieceType type){
        int row = kingPosition.getRow();
        int column = kingPosition.getColumn();
        for (int[] movement:movementType){
            int addRow=movement[0];
            int addColumn=movement[1];
            do{
                ChessPosition newPosition = new ChessPosition(row+movement[0],column+movement[1]);
                if(newPosition.getRow()>0 && newPosition.getRow()<=8 && newPosition.getColumn()>0 && newPosition.getColumn()<=8){
                    if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != board.getPiece(kingPosition).getTeamColor()){
                        return true;
                    }
                    else if(board.getPiece(newPosition) != null){
                        movement[2]=0;
                    }
                }
                else{movement[2]=0;}
                movement[0]+=addRow;
                movement[1]+=addColumn;
            }while(movement[2]==1);
        }

        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}

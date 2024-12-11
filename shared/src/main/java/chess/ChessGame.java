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
    private TeamColor teamTurn = TeamColor.WHITE;
    private TeamColor winner = null;
    private ChessBoard board = new ChessBoard();
    public ChessGame() {
        board.resetBoard();
    }
    public ChessGame(ChessBoard board) {
        this.board = board;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public TeamColor getWinner() {
        return winner;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }
    public void setWinner(TeamColor team) {
        winner = team;
    }

    public void changeTeamTurn(){
        if(teamTurn==TeamColor.WHITE){
            teamTurn=TeamColor.BLACK;
        }else if(teamTurn==TeamColor.BLACK){
            teamTurn=TeamColor.WHITE;
        }
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
        ArrayList<ChessMove> moves = new ArrayList<>();
        ArrayList<ChessMove> oldMoves = (ArrayList<ChessMove>) board.getPiece(startPosition).pieceMoves(board, startPosition);
        TeamColor color = board.getPiece(startPosition).getTeamColor();
        for(ChessMove move : oldMoves){
            ChessPiece capturedPiece=board.getPiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            board.addPiece(move.getStartPosition(),null);
            if(!isInCheck(color)){

                moves.add(move);
            }

            board.addPiece(move.getStartPosition(), board.getPiece(move.getEndPosition()));
            board.addPiece(move.getEndPosition(),capturedPiece);


        }


        return moves;
        //only add valid moves
    }

    public Collection<ChessMove> allValidMoves(TeamColor color){
        ArrayList<ChessMove> moves=new ArrayList<>();

        for (int i=1;i<=8;i++){
            for (int j=1;j<=8;j++){
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if(piece != null && piece.getTeamColor() == color){
                    moves.addAll(validMoves(pos));
                }
            }
        }

        return moves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece startPiece = board.getPiece(move.getStartPosition());
        if (startPiece == null) {
            throw new InvalidMoveException("No piece selected.");
        } else if (startPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Out of turn.");
        } else if (!allValidMoves(startPiece.getTeamColor()).contains(move)) {
            throw new InvalidMoveException("Invalid move");
        } else {
            if(move.getPromPiece()!=null){
                ChessPiece promotionPiece = new ChessPiece(teamTurn, move.getPromPiece());
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                board.removePiece(move.getStartPosition());

                board.addPiece(move.getEndPosition(), promotionPiece);
            }
            else{
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            }
            board.removePiece(move.getStartPosition());
        }



        if(isInCheck(teamTurn)){
            throw new InvalidMoveException("Can not move into check.");
        }
        changeTeamTurn();
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
                ChessPiece checkPiece = board.getPiece(new ChessPosition(i,j));
                if(checkPiece != null) {
                    if (checkPiece.getPieceType() == ChessPiece.PieceType.KING && checkPiece.getTeamColor() == teamColor) {
                        pos = new ChessPosition(i, j);
                    }
                }
            }
        }

        return isInDanger(pos, teamColor);
    }

    //Check spot in danger
    private boolean isInDanger(ChessPosition pos, TeamColor color){

        boolean isInCheck;

        int[][] kingMove={{1,0,0},{1,1,0},{0,1,0},{-1,1,0},{-1,0,0},{-1,-1,0},{0,-1,0},{1,-1,0}};
        isInCheck= checkPiece(pos,color,kingMove, ChessPiece.PieceType.KING);

        int[][] queenMove={{1,0,1},{1,1,1},{0,1,1},{-1,1,1},{-1,0,1},{-1,-1,1},{0,-1,1},{1,-1,1}};
        isInCheck= isInCheck || checkPiece(pos,color,queenMove, ChessPiece.PieceType.QUEEN);

        int[][] bishopMove={{1,1,1},{-1,1,1},{-1,-1,1},{1,-1,1}};
        isInCheck=isInCheck || checkPiece(pos,color,bishopMove, ChessPiece.PieceType.BISHOP);

        int[][] knightMove={{2,1,0},{1,2,0},{-1,2,0},{-2,1,0},{-2,-1,0},{-1,-2,0},{1,-2,0},{2,-1,0}};
        isInCheck=isInCheck || checkPiece(pos,color,knightMove, ChessPiece.PieceType.KNIGHT);

        int[][] rookMove={{1,0,1}, {0,1,1},{-1,0,1},{0,-1,1}};
        isInCheck=isInCheck || checkPiece(pos,color,rookMove, ChessPiece.PieceType.ROOK);

        int direction=1;
        if(color==TeamColor.BLACK){
            direction=-1;
        }
        if (pos.getRow() <= 7.5-0.5*direction && pos.getRow() >= 1.5-0.5*direction) {
            if(pos.getColumn()<8){
                ChessPiece pawnTest = board.getPiece(new ChessPosition(pos.getRow()+direction, pos.getColumn()+1));
                if(pawnTest != null && pawnTest.getPieceType() == ChessPiece.PieceType.PAWN && pawnTest.getTeamColor() !=color){
                    isInCheck=true;
                }
            }

            if(pos.getColumn()>1){
                ChessPiece pawnTest = board.getPiece(new ChessPosition(pos.getRow()+direction, pos.getColumn()-1));
                if(pawnTest != null && pawnTest.getPieceType() == ChessPiece.PieceType.PAWN && pawnTest.getTeamColor() !=color){
                    isInCheck=true;
                }
            }

        }


        return isInCheck;
    }



    //Checks the pieces on the board to see if any of them can move to the position of the king.
    private boolean checkPiece(ChessPosition kingPosition, TeamColor color, int[][]movementType, ChessPiece.PieceType type){
        int row = kingPosition.getRow();
        int column = kingPosition.getColumn();
        for (int[] movement:movementType){
            int addRow=movement[0];
            int addColumn=movement[1];
            do{
                ChessPosition newPosition = new ChessPosition(row+movement[0],column+movement[1]);
                int newRow=newPosition.getRow();
                int newCol=newPosition.getColumn();
                if(newRow>0 && newRow<=8 && newCol > 0 && newCol <= 8) {
                    ChessPiece newPiece= board.getPiece(newPosition);
                    if (newPiece != null && newPiece.getTeamColor() != color && newPiece.getPieceType() == type) {
                        return true;
                    } else if (board.getPiece(newPosition) != null) {
                        movement[2] = 0;
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

        return isInCheck(teamColor) && allValidMoves(teamColor).isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && allValidMoves(teamColor).isEmpty();
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

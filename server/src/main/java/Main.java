import chess.*;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import server.*;
import service.ChessService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        try {
            int port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            DataAccess dataAccess = new MemoryDataAccess();


            var service = new ChessService(dataAccess);
            var server = new Server();
            port = server.run(port);
            System.out.printf("Server started on port %d%n", port);
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }



        System.out.println("♕ 240 Chess Server: " + piece);
    }
}
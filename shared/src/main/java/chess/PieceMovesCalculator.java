package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    static void addMove(ChessBoard board, ChessPosition position, ArrayList<ChessMove> moves, int r, int c, ChessPiece me){
        if(r < 9 && c < 9 && c > 0 && r > 0)
        {
            ChessPosition newplace = new ChessPosition(r, c);
            if ((board.getPiece(newplace) == null) || (board.getPiece(newplace).getTeamColor() != me.getTeamColor()))
                moves.add(new ChessMove(position, newplace, null));
        }
    }
}
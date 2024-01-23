package chess.PieceMovers;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves implements chess.PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece me = board.getPiece(position);
        int [] dirs = {-1, 0, 1};
        for (int dr: dirs)
            for (int dc: dirs)
            {
                if (dc == 0 && dr == 0)
                    continue;
                int newr = r + dr;
                int newc = c + dc;
                PieceMovesCalculator.addMoveNoBreak(board, position, moves, newr, newc, me);
            }


        return moves;
    }
}

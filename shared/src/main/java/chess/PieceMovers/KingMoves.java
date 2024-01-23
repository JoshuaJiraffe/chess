package chess.PieceMovers;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

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
                if(newr < 9 && newc < 9 && newc > 0 && newr > 0)
                {
                    ChessPosition newplace = new ChessPosition(newr, newc);
                    if ((board.getPiece(newplace) == null) || (board.getPiece(newplace).getTeamColor() != me.getTeamColor()))
                        moves.add(new ChessMove(position, newplace, null));
                }
            }


        return moves;
    }
}

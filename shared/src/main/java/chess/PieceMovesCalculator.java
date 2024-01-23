package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    static void addMoveNoBreak(ChessBoard board, ChessPosition position, ArrayList<ChessMove> moves, int r, int c, ChessPiece me){
        if(r < 9 && c < 9 && c > 0 && r > 0)
        {
            ChessPosition newplace = new ChessPosition(r, c);
            if ((board.getPiece(newplace) == null) || (board.getPiece(newplace).getTeamColor() != me.getTeamColor()))
                moves.add(new ChessMove(position, newplace, null));
        }
    }

    static void addMoveYesBreak(ChessBoard board, ChessPosition position, ArrayList<ChessMove> moves, int r, int c, int[] dr, int[] dc, int i, ChessPiece me)
    {
        for (int j = 1; j < 8; j++) {
            int newr = r + dr[i] * j;
            int newc = c + dc[i] * j;
            if (newr < 9 && newc < 9 && newc > 0 && newr > 0) {
                ChessPosition newplace = new ChessPosition(newr, newc);
                if (board.getPiece(newplace) != null) {
                    if (board.getPiece(newplace).getTeamColor() != me.getTeamColor())
                        moves.add(new ChessMove(position, newplace, null));
                    return;
                } else
                    moves.add(new ChessMove(position, newplace, null));
            }
        }
    }
}
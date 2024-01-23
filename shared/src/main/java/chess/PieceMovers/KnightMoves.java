package chess.PieceMovers;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves implements chess.PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece me = board.getPiece(position);
        int [] dr = {-2, -2, -1, -1, 1, 1, 2, 2};
        int [] dc = {-1, 1, -2, 2, -2, 2, -1, 1};
        for (int i = 0; i < 8; i++) {
            int newr = r + dr[i];
            int newc = c + dc[i];
            PieceMovesCalculator.addMoveNoBreak(board, position, moves, newr, newc, me);
        }


        return moves;
    }
}

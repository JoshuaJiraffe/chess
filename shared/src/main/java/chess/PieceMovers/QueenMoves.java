package chess.PieceMovers;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves implements chess.PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position)
    {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece me = board.getPiece(position);
        int [] dr = {-1, 0, 1, 1, 1, 0, -1, -1};
        int [] dc = {-1, -1, -1, 0, 1, 1, 1, 0};
        for (int i = 0; i < 8; i ++)
            PieceMovesCalculator.addMoveYesBreak(board, position, moves, r, c, dr, dc, i, me);
        return moves;
    }
}

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
        int homerow = (me.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : 8;

        if((!me.getMoved()) && position.equals(new ChessPosition(homerow, 5)))
        {
            ChessPiece kingRook = board.getPiece(new ChessPosition(homerow, 8));
            ChessPiece queenRook = board.getPiece(new ChessPosition(homerow, 1));
            ChessPiece test;
            boolean clear = true;

            //        King's side castle
            if((kingRook != null) && (!kingRook.getMoved()))
            {
                for(int i = 6; i < 8; i++)
                {
                    test = board.getPiece(new ChessPosition(homerow, i));
                    if(test != null)
                        clear = false;
                }
                if(clear)
                {
                    ChessMove kingCastle = new ChessMove(position, new ChessPosition(homerow, 7), null);
                    kingCastle.setCastleMove();
                    moves.add(kingCastle);
                }
            }

            //        Queen's side castle
            if((queenRook != null) && (!queenRook.getMoved()))
            {
                for(int i = 4; i > 2; i--)
                {
                    test = board.getPiece(new ChessPosition(homerow, i));
                    if(test != null)
                        clear = false;
                }
                if(clear)
                {
                    ChessMove queenCastle = new ChessMove(position, new ChessPosition(homerow, 3), null);
                    queenCastle.setCastleMove();
                    moves.add(queenCastle);
                }
            }
        }
        return moves;
    }
}

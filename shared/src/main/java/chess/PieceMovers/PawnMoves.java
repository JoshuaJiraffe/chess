package chess.PieceMovers;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves implements chess.PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece me = board.getPiece(position);
        ChessGame.TeamColor color = me.getTeamColor();
        int dir, dirs, enpassantrow, startrow;
        int[] capc = {-1, 1};
        if (color == ChessGame.TeamColor.WHITE)
        {
            dir = 1;
            dirs = 2;
            enpassantrow = 5;
            startrow = 2;
        }
        else
        {
            dir = -1;
            dirs = -2;
            enpassantrow = 4;
            startrow = 7;
        }
        int newr = r + dir;
        if(0 < newr && newr < 9)
        {
            ChessPosition newplace = new ChessPosition(newr, c);
            if(board.getPiece(newplace) == null)
            {
                if(newr == 1 || newr == 8)
                    promotion(position, newplace, moves);
                else {
                    moves.add(new ChessMove(position, newplace, null));
                    if (position.getRow() == startrow)
                    {
                        ChessPosition newestplace = new ChessPosition(r + dirs, c);
                        if(board.getPiece(newestplace) == null)
                            moves.add(new ChessMove(position, newestplace, null));
                    }
                }
            }
        }
        if (r == enpassantrow)
        {
            if(c > 1)
            {
                ChessPosition left = new ChessPosition(r, c - 1);
                if (left.equals(board.getEnPassantableLocation()))
                    moves.add(new EnPassantMove(position, new ChessPosition(r+dir, c-1)));
            }
            if (c < 8)
            {
                ChessPosition right = new ChessPosition(r, c+1);
                if (right.equals(board.getEnPassantableLocation()))
                    moves.add(new EnPassantMove(position, new ChessPosition(r+dir, c+1)));
            }
        }
        for(int cc: capc)
        {
            int newc = c + cc;
            if(newc > 0 && newc < 9)
            {
                ChessPosition newplace = new ChessPosition(newr, newc);
                if ((board.getPiece(newplace) != null) && (board.getPiece(newplace).getTeamColor() != me.getTeamColor()))
                {
                    if(newr == 1 || newr == 8)
                        promotion(position, newplace, moves);
                    else
                        moves.add(new ChessMove(position, newplace, null));
                }

            }
        }



        return moves;
    }
    private void promotion(ChessPosition position, ChessPosition newplace, ArrayList<ChessMove> moves)
    {
        for (ChessPiece.PieceType type : ChessPiece.PieceType.values())
            if (type != ChessPiece.PieceType.PAWN && type != ChessPiece.PieceType.KING)
                moves.add(new ChessMove(position, newplace, type));
    }
}

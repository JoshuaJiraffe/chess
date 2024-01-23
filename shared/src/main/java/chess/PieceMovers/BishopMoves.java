package chess.PieceMovers;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public class BishopMoves implements chess.PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece me = board.getPiece(position);
        int [] dr = {-1, -1, 1, 1};
        int [] dc = {-1, 1, -1, 1};
        for(int i = 0; i < 4; i++)
            PieceMovesCalculator.addMoveYesBreak(board, position, moves, r, c, dr, dc, i, me);
        return moves;
    }
}




//        while(r < 8 && c < 8)
//        {
//            r+=1;
//            c+=1;
//            ChessPosition end = new ChessPosition(r,c);
//            if (board.getPiece(end) != null) {
//                if (board.getPiece(end).getTeamColor() != me.getTeamColor())
//                    moves.add(new ChessMove(position, end, null));
//                break;
//            }
//            else
//                moves.add(new ChessMove(position, end, null));
//        }
//        r = position.getRow();
//        c = position.getColumn();
//        while(r < 8 && c > 1)
//        {
//            r+=1;
//            c-=1;
//            ChessPosition end = new ChessPosition(r,c);
//            if (board.getPiece(end) != null){
//                if (board.getPiece(end).getTeamColor() != me.getTeamColor())
//                    moves.add(new ChessMove(position, end, null));
//                break;
//            }
//            else
//                moves.add(new ChessMove(position, end, null));
//        }
//        r = position.getRow();
//        c = position.getColumn();
//        while(r > 1 && c < 8)
//        {
//            r-=1;
//            c+=1;
//            ChessPosition end = new ChessPosition(r,c);
//            if (board.getPiece(end) != null){
//                if (board.getPiece(end).getTeamColor() != me.getTeamColor())
//                    moves.add(new ChessMove(position, end, null));
//                break;
//            }
//            else
//                moves.add(new ChessMove(position, end, null));
//        }
//        r = position.getRow();
//        c = position.getColumn();
//        while(r > 1 && c > 1)
//        {
//            r-=1;
//            c-=1;
//            ChessPosition end = new ChessPosition(r,c);
//            if (board.getPiece(end) != null){
//                if (board.getPiece(end).getTeamColor() != me.getTeamColor())
//                    moves.add(new ChessMove(position, end, null));
//                break;
//            }
//            else
//                moves.add(new ChessMove(position, end, null));
//        }

package chess;

public class Castle
{
    public static void Castle(ChessPosition start, ChessPosition end, ChessPiece moving_piece, ChessBoard bored)
    {
        ChessPosition rookStart, rookEnd;
        ChessPiece rook;
        if(end.getColumn() == 7)
        {
            rookStart = new ChessPosition(start.getRow(), 8);
            rook = bored.getPiece(rookStart);
            rookEnd = new ChessPosition(start.getRow(), 6);
        }
        else
        {
            rookStart = new ChessPosition(start.getRow(), 1);
            rook = bored.getPiece(rookStart);
            rookEnd = new ChessPosition(start.getRow(), 4);
        }
        bored.addPiece(start, null);
        bored.addPiece(rookStart, null);
        bored.addPiece(end, moving_piece);
        bored.addPiece(rookEnd, rook);
    }
}

package chess;

public class EnPassant
{
    public static void makeEnPassMove(ChessPosition start, ChessPosition end, ChessPiece moving_piece, ChessBoard bored)
    {
        ChessPosition killPosition = new ChessPosition(start.getRow(), end.getColumn());
        bored.addPiece(end, moving_piece);
        bored.addPiece(start, null);
        bored.addPiece(killPosition, null);
    }
}

package chess;

public class EnPassant
{
    public static void makeEnPassMove(ChessPosition start, ChessPosition end, ChessPiece movingPiece, ChessBoard bored)
    {
        ChessPosition killPosition = new ChessPosition(start.getRow(), end.getColumn());
        bored.addPiece(end, movingPiece);
        bored.addPiece(start, null);
        bored.addPiece(killPosition, null);
    }
}

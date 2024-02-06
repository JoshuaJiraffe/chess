package chess;

public class EnPassantMove extends ChessMove
{
    private final Direction moveDirection;
    public EnPassantMove(ChessPosition startPosition, ChessPosition endPosition)
    {
        super(startPosition, endPosition, null);
        moveDirection = (endPosition.getColumn() - startPosition.getColumn() == 1) ? Direction.RIGHT : Direction.LEFT;

    }

    public Direction getMoveDirection()
    {
        return moveDirection;
    }

    public enum Direction
    {
        LEFT,
        RIGHT
    }

}

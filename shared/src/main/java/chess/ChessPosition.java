package chess;

import java.util.Objects;
import java.util.Map;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;
    private final char not;
    private final Map<Integer, Character> colNot = Map.of(
            1, 'a',
            2, 'b',
            3, 'c',
            4, 'd',
            5, 'e',
            6, 'f',
            7, 'g',
            8, 'h'

    );
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
        this.not = colNot.get(col);
    }

    public ChessPosition(ChessPosition other)
    {
        this.row = other.row;
        this.col = other.col;
        this.not = other.not;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {

        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {

        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChessPosition that = (ChessPosition) o;
        return getRow() == that.getRow() && getColumn() == that.getColumn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), col);
    }

    @Override
    public String toString() {
        return not + ""  + row;
    }
}

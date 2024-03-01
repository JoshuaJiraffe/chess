package chess;

import chess.PieceMovers.*;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final ChessPiece.PieceType type;
    private final int value;
    private boolean hasMoved;
    private int numMoves;

    private final Map<PieceType, Integer> pieceValues = Map.of(
        PieceType.PAWN, 1,
        PieceType.KNIGHT, 3,
        PieceType.BISHOP, 3,
        PieceType.ROOK, 5,
        PieceType.QUEEN, 9,
        PieceType.KING, 0
    );
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
        this.value = pieceValues.get(type);
        this.hasMoved = false;
        this.numMoves = 0;

    }

    public ChessPiece(ChessPiece other)
    {
        this.color = other.color;
        this.type = other.type;
        this.value = pieceValues.get(type);
        this.hasMoved = other.hasMoved;
        this.numMoves = other.numMoves;
    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    public int getValue()
    {
        return this.value;
    }

    public boolean getMoved() { return this.hasMoved; }


    public void move() {
        this.hasMoved = true;
        this.numMoves += 1;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator pedazito = switch (this.type) {
            case PAWN -> new PawnMoves();
            case KNIGHT -> new KnightMoves();
            case BISHOP -> new BishopMoves();
            case ROOK -> new RookMoves();
            case QUEEN -> new QueenMoves();
            case KING -> new KingMoves();
            default -> {
                throw new RuntimeException("Piece probably doesn't exist");
            }
        };
        return pedazito.pieceMoves(board, myPosition);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return getValue() == that.getValue() && color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type, getValue());
    }

    @Override
    public String toString() {
        return color.toString() + " " + type.toString();
    }
}


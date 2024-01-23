package chess;

import chess.PieceMovers.*;

import java.util.Collection;
import java.util.Map;

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
            default -> throw new RuntimeException("Piece probably doesn't exist");
        };
        return pedazito.pieceMoves(board, myPosition);

    }
}

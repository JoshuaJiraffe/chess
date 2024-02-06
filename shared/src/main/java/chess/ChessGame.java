package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard bored;
    public ChessGame() {
        turn = TeamColor.WHITE;
        bored = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = bored.getPiece(startPosition);
        ArrayList<ChessMove> possibleMoves = (ArrayList<ChessMove>) piece.pieceMoves(bored, startPosition);
        ArrayList<ChessMove> realMoves = new ArrayList<>();
        if (piece == null)
            return null;
        else
        {
            for(ChessMove move: possibleMoves)
                if(testMove(move, bored))
                    realMoves.add(move);
        }
        return realMoves;
    }

    private boolean testMove(ChessMove move, ChessBoard board)
    {
        ChessBoard testBoard = new ChessBoard(board);
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece moving_piece = testBoard.getPiece(start);
        testBoard.addPiece(start, null);
        testBoard.addPiece(end, moving_piece);
        return !isInCheck(turn);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece moving_piece;
        if(move.getPromotionPiece() == null)
           moving_piece = bored.getPiece(start);
        else
            moving_piece = new ChessPiece(turn, move.getPromotionPiece());
        if(moving_piece.getTeamColor() != turn)
            throw new InvalidMoveException("It's not " + moving_piece.getTeamColor().toString().toLowerCase() + "'s turn!");
        if(!validMoves(start).contains(move))
            throw new InvalidMoveException("That move is not valid");
        else
        {
            bored.addPiece(start, null);
            bored.addPiece(end, moving_piece);
            if(moving_piece.getPieceType() == ChessPiece.PieceType.KING)
                bored.setkingloc(end, turn);
            turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPiece piece;
        ChessPosition position;
        ChessPosition king_location = bored.getkingloc(teamColor);
        for(int r = 1; r <= 8; r++)
            for(int c = 1; c <= 8; c++)
            {
                position = new ChessPosition(r, c);
                piece = bored.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor)
                {
                    for(ChessMove move : piece.pieceMoves(bored, position))
                    {
                        if (move.getEndPosition().equals(king_location))
                        {
                            return true;
                        }
                    }
                }
            }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && isInStalemate(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPiece piece;
        ChessPosition position;
        for(int r = 1; r <= 8; r++)
            for(int c = 1; c <=8; c++)
            {
                position = new ChessPosition(r, c);
                piece = bored.getPiece(position);
                if(piece != null && piece.getTeamColor() == teamColor)
                    if(!validMoves(position).isEmpty())
                        return false;
            }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        bored = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return bored;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(bored, chessGame.bored);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, bored);
    }

    @Override
    public String toString() {
        return bored + "\n It is " + turn + "'s turn";
    }
}

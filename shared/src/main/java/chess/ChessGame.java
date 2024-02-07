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
        if (piece == null)
            return null;
        ArrayList<ChessMove> possibleMoves = (ArrayList<ChessMove>) piece.pieceMoves(bored, startPosition);
        System.out.println("possible moves are:" + possibleMoves);
        ArrayList<ChessMove> realMoves = new ArrayList<>();
        for(ChessMove move: possibleMoves)
            if(move.isPassantMove() || testMove(move, bored))
            {
                realMoves.add(move);
            }
        System.out.println("real moves are: " + realMoves);
        return realMoves;
    }

    private boolean testMove(ChessMove move, ChessBoard board)
    {
        ChessBoard testBoard = new ChessBoard(board);
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece moving_piece = testBoard.getPiece(start);
        if(move.isCastleMove())
        {
            System.out.println("yay");
            ChessPosition test;
            int homerow = (moving_piece.getTeamColor() == TeamColor.WHITE) ? 1 : 8;
            if(end.getColumn() == 7)
            {
                for(int i = 5; i <= 7; i++)
                {
                    test = new ChessPosition(homerow, i);
                    testBoard.addPiece(start, null);
                    testBoard.addPiece(test, moving_piece);
                    if(inCheckHelper(moving_piece.getTeamColor(), testBoard))
                        return false;
                }
            }
            if(end.getColumn() == 3)
            {
                for(int i = 5; i >= 3; i--)
                {
                    test = new ChessPosition(homerow, i);
                    testBoard.addPiece(start, null);
                    testBoard.addPiece(test, moving_piece);
                    if(inCheckHelper(moving_piece.getTeamColor(), testBoard))
                        return false;
                }
            }
            return true;
        }
        else
        {
            testBoard.addPiece(start, null);
            testBoard.addPiece(end, moving_piece);
            return !inCheckHelper(moving_piece.getTeamColor(), testBoard);
        }
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

//        Error Handling
        if(moving_piece.getTeamColor() != turn)
            throw new InvalidMoveException("It's not " + moving_piece.getTeamColor().toString().toLowerCase() + "'s turn!");
        if(!validMoves(start).contains(move))
            throw new InvalidMoveException("That move is not valid");

//        Movement Handling
        else
        {
            if(moving_piece.getPieceType() == ChessPiece.PieceType.PAWN && start.getColumn() != end.getColumn() && bored.getPiece(end) == null)
                move.setPassantMove();
            else if(moving_piece.getPieceType() == ChessPiece.PieceType.KING && Math.abs(start.getColumn() - end.getColumn()) == 2)
                move.setCastleMove();
            if(move.isPassantMove())
                makeEnPassMove(start, end, moving_piece);
            else if (move.isCastleMove())
                Castle(start, end, moving_piece);
            else
            {
                bored.addPiece(start, null);
                bored.addPiece(end, moving_piece);
            }
            int rowdif = Math.abs(start.getRow() - end.getRow());
            if (moving_piece.getPieceType() == ChessPiece.PieceType.PAWN && rowdif == 2)
                bored.setEnPassantableLocation(end);
            else
                bored.setEnPassantableLocation(null);

            moving_piece.move();
            turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        }


        for(int r = 1; r < 9; r ++)
        {
            for (int c = 1; c < 9; c++)
                System.out.print(bored.getPiece(new ChessPosition(r, c)) + " ");
            System.out.println();
        }
    }

    public void makeEnPassMove(ChessPosition start, ChessPosition end, ChessPiece moving_piece)
    {
        ChessPosition killPosition = new ChessPosition(start.getRow(), end.getColumn());
        bored.addPiece(end, moving_piece);
        System.out.println("kill position is " + killPosition);
        bored.addPiece(start, null);
        bored.addPiece(killPosition, null);
    }

    public void Castle(ChessPosition start, ChessPosition end, ChessPiece moving_piece)
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

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return inCheckHelper(teamColor, bored);
    }

    public boolean inCheckHelper(TeamColor teamColor, ChessBoard board)
    {
        ChessPiece piece;
        ChessPosition position;
        ChessPosition king_location = board.getkingloc(teamColor);
        for(int r = 1; r <= 8; r++)
            for(int c = 1; c <= 8; c++)
            {
                position = new ChessPosition(r, c);
                piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor)
                {
                    for(ChessMove move : piece.pieceMoves(board, position))
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
        return turn == chessGame.turn && getBoard().equals(chessGame.getBoard());
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

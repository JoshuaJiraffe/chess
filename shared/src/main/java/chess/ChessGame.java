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
    private boolean gameOver;
    public ChessGame() {
        turn = TeamColor.WHITE;
        bored = new ChessBoard();
        gameOver = false;
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

    public boolean isGameOver()
    {
        return gameOver;
    }

    public void endGame()
    {
        this.gameOver = true;
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
        ArrayList<ChessMove> realMoves = new ArrayList<>();
        for(ChessMove move: possibleMoves)
            if(move.isPassantMove() || testMove(move, bored))
            {
                realMoves.add(move);
            }
        return realMoves;
    }

    private boolean testMove(ChessMove move, ChessBoard board)
    {
        ChessBoard testBoard = new ChessBoard(board);
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece movingPiece = testBoard.getPiece(start);
        if(move.isCastleMove())
        {
            ChessPosition test;
            int homerow = (movingPiece.getTeamColor() == TeamColor.WHITE) ? 1 : 8;
            if(end.getColumn() == 7)
            {
                for(int i = 5; i <= 7; i++)
                {
                    test = new ChessPosition(homerow, i);
                    if(!testHelper(test, start, homerow, i, movingPiece, testBoard))
                        return false;
                }
            }
            if(end.getColumn() == 3)
            {
                for(int i = 5; i >= 3; i--)
                {
                    test = new ChessPosition(homerow, i);
                    if(!testHelper(test, start, homerow, i, movingPiece, testBoard))
                        return false;
                }
            }
            return true;
        }
        else
        {
            testBoard.addPiece(start, null);
            testBoard.addPiece(end, movingPiece);
            return !inCheckHelper(movingPiece.getTeamColor(), testBoard);
        }
    }
    public boolean testHelper(ChessPosition test, ChessPosition start, int homerow, int i, ChessPiece movingPiece, ChessBoard testBoard)
    {
        test = new ChessPosition(homerow, i);
        testBoard.addPiece(start, null);
        testBoard.addPiece(test, movingPiece);
        if(inCheckHelper(movingPiece.getTeamColor(), testBoard))
            return false;
        return true;
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
        ChessPiece movingPiece;
        if(move.getPromotionPiece() == null)
           movingPiece = bored.getPiece(start);
        else
            movingPiece = new ChessPiece(turn, move.getPromotionPiece());

//        Error Handling
        if(movingPiece.getTeamColor() != turn)
            throw new InvalidMoveException("It's not " + movingPiece.getTeamColor().toString().toLowerCase() + "'s turn!");
        if(!validMoves(start).contains(move))
            throw new InvalidMoveException("That move is not valid");

//        Movement Handling
        else
        {
            if(movingPiece.getPieceType() == ChessPiece.PieceType.PAWN && start.getColumn() != end.getColumn() && bored.getPiece(end) == null)
                move.setPassantMove();
            else if(movingPiece.getPieceType() == ChessPiece.PieceType.KING && Math.abs(start.getColumn() - end.getColumn()) == 2)
                move.setCastleMove();
            if(move.isPassantMove())
                EnPassant.makeEnPassMove(start, end, movingPiece, bored);
            else if (move.isCastleMove())
                Castling.castle(start, end, movingPiece, bored);
            else
            {
                bored.addPiece(start, null);
                bored.addPiece(end, movingPiece);
            }
            int rowdif = Math.abs(start.getRow() - end.getRow());
            if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN && rowdif == 2)
                bored.setEnPassantableLocation(end);
            else
                bored.setEnPassantableLocation(null);

            movingPiece.move();
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
        return inCheckHelper(teamColor, bored);
    }

    public boolean inCheckHelper(TeamColor teamColor, ChessBoard board)
    {
        ChessPiece piece;
        ChessPosition position;
        ChessPosition kingLocation = board.getkingloc(teamColor);
        for(int r = 1; r <= 8; r++)
            for(int c = 1; c <= 8; c++)
            {
                position = new ChessPosition(r, c);
                piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor)
                {
                    for(ChessMove move : piece.pieceMoves(board, position))
                    {
                        if (move.getEndPosition().equals(kingLocation))
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
        return turn == chessGame.turn && getBoard().equals(chessGame.getBoard())  && gameOver == chessGame.isGameOver();
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, bored, gameOver);
    }

    @Override
    public String toString() {
        return bored + "\n It is " + turn + "'s turn";
    }
}

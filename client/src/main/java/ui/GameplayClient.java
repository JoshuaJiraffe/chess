package ui;

import chess.*;
import model.AuthData;
import websocket.GameHandler;
import websocket.WebSocketFacade;

import java.io.PrintStream;
import java.rmi.ServerException;
import java.util.*;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_ALL;
import static ui.PostLoginClient.isInteger;

public class GameplayClient
{
    private final ServerFacade server;
    private final String serverUrl;
    Scanner scanner;
    private PrintStream out;
    private AuthData auth;
    private int gameID;
    private ChessGame.TeamColor playerColor;
    private final GameHandler gameHand;
    private final WebSocketFacade ws;
    private final boolean observer;
    private ChessGame game;

    private final Map<Character, Integer> colNum = Map.of(
            'a', 1,
            'b', 2,
            'c', 3,
            'd', 4,
            'e', 5,
            'f', 6,
            'g', 7,
            'h', 8
            );

    public GameplayClient(ServerFacade server, String serverURL, AuthData auth, int gameID, ChessGame.TeamColor color, Scanner scanner, PrintStream out) throws ServerException
    {
        this.server = server;
        this.serverUrl = serverURL;
        this.auth = auth;
        this.gameID = gameID;
        this.playerColor = color;
        this.scanner = scanner;
        this.out = out;
        this.gameHand = new GameHandler();
        ws = new WebSocketFacade(serverURL, gameHand);
        if(color == null)
        {
            observer = true;
            ws.joinObserver();
        }
        else
        {
            observer = false;
            ws.joinPlayer();
        }
    }

    public void run()
    {
        out.println(ERASE_SCREEN + RESET_TEXT);
        out.println(SET_BG_COLOR_DARK_GREY);
        out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + BLACK_PAWN + "Let the game begin!" + BLACK_PAWN);
        out.println(RESET_TEXT);
        try
        {
            printBoard();
            boolean quit = false;
            while(!quit)
            {
                String line = scanner.nextLine();
                quit = this.eval(line);
            }
        } catch (Throwable e) {
            var msg = e.toString();
            out.println(msg);
        }
        out.println(SET_TEXT_COLOR_MAGENTA + SET_TEXT_ITALIC + "You lost. Or maybe you won. Who knows?" + RESET_TEXT);
    }

    public boolean eval(String input) throws ServerException
    {
        var quitting = false;
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (cmd) {
                case "move" -> makeMove();
                case "redraw" -> printBoard();
                case "resign" -> resign();
                case "leave" -> { quitting = true;
                    leave(); }
                case "highlight" -> highlightMoves();
                default -> help();
            };
        } catch (ServerException ex) {
            System.out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + ex.getMessage() + RESET_TEXT);
        }
        return quitting;
    }
    public void help()
    {
        out.print(RESET_TEXT);
        out.println(SET_TEXT_COLOR_MAGENTA + SET_TEXT_BOLD + "Here are the available commands:\n" + RESET_TEXT_BOLD_FAINT +
                SET_TEXT_COLOR_YELLOW + "redraw " + SET_TEXT_COLOR_BLUE + "- see the game board\n" +
                SET_TEXT_COLOR_YELLOW + "highlight " + SET_TEXT_COLOR_BLUE + "- see all possible moves for a piece\n" +
                SET_TEXT_COLOR_YELLOW + "help " + SET_TEXT_COLOR_BLUE + "- see available commands\n" +
                SET_TEXT_COLOR_YELLOW + "leave " + SET_TEXT_COLOR_BLUE + "- stop playing because you're bored"
        );
        if(!observer)
            out.println(SET_TEXT_COLOR_YELLOW + "move " + SET_TEXT_COLOR_BLUE + "- move one of your pieces\n" +
                    SET_TEXT_COLOR_YELLOW + "resign " + SET_TEXT_COLOR_BLUE + "- announce defeat because you're bad"
            );
        out.println(RESET_TEXT);
    }

    private void makeMove() throws ServerException
    {
        out.println(RESET_TEXT);
        if(observer)
        {
            help();
            return;
        }
        else if(game.getTeamTurn() != playerColor)
            out.print(SET_TEXT_COLOR_RED + "It's not your turn. Learn some patience");
        else
        {
            ChessBoard board = game.getBoard();
            out.println(SET_TEXT_COLOR_MAGENTA + "Let's see what tricks you have up your sleeve");


//            Get starting location/piece
            out.println(SET_TEXT_COLOR_YELLOW + "What is the location of the piece you want to move?");
            int c = getColumn();
            int r = getRow();
            ChessPosition start = new ChessPosition(r, c);
            ChessPiece piece = board.getPiece(start);
            while((piece == null) || (piece.getTeamColor() != playerColor) || (game.validMoves(start).isEmpty()))
            {
                out.println(SET_TEXT_COLOR_RED + "That's not a piece you can move");
                out.println(SET_TEXT_COLOR_YELLOW + "What is the location of the piece you want to move?");
                start = new ChessPosition(getRow(), getColumn());
                piece = board.getPiece(start);
            }
            out.println(SET_TEXT_COLOR_MAGENTA + "You have chosen to move your " + piece.getPieceType().toString().toLowerCase() + ". Here are your valid moves");

//            Get ending location
            HashSet<ChessPosition> endPositions = new HashSet<>(highlight(start));
            out.println(SET_TEXT_COLOR_YELLOW + "Where do you want to move to?");
            ChessPosition end = new ChessPosition(getRow(), getColumn());
            while(!endPositions.contains(end))
            {
                out.println(SET_TEXT_COLOR_RED + "That is not a valid move! Cheater");
                out.println(SET_TEXT_COLOR_YELLOW + "Where do you want to move to?");
                end = new ChessPosition(getRow(), getColumn());
            }
            ChessPiece.PieceType promotion = null;
            if((piece.getPieceType() == ChessPiece.PieceType.PAWN) && (r == 8 || r == 1))
                promotion = getPromotionPiece();
            ChessMove move = new ChessMove(start, end, promotion);

//          Do Websocket stuff


        }
        out.println(RESET_TEXT);
    }

    private boolean isCol(String c)
    {
        if(c.length() > 1)
            return false;
        return colNum.containsKey(c.charAt(0));
    }

    private int getColumn()
    {
        out.print(SET_TEXT_COLOR_YELLOW + "Column: " + SET_TEXT_COLOR_WHITE);
        String col = scanner.nextLine().toLowerCase();
        if(isCol(col))
            return colNum.get(col.charAt(0));
        if(isInteger(col))
        {
            int c = Integer.parseInt(col);
            if((c > 0) && (c < 9))
                return c;
            out.println(SET_TEXT_COLOR_RED + "That is not a valid column number");
        }
        else
            out.println(SET_TEXT_COLOR_RED + "The column must either be a number or valid letter notation");
        return getColumn();
    }

    private int getRow()
    {
        out.print(SET_TEXT_COLOR_YELLOW + "Row: " + SET_TEXT_COLOR_WHITE);
        String row = scanner.nextLine().toLowerCase();
        if(isInteger(row))
        {
            int r = Integer.parseInt(row);
            if((r > 0) && (r < 9))
                return r;
            out.println(SET_TEXT_COLOR_RED + "That is not a valid row number");
        }
        else
            out.println(SET_TEXT_COLOR_RED + "The column must either be a number or valid letter notation");
        return getRow();
    }

    private ChessPiece.PieceType getPromotionPiece()
    {
        out.println(SET_TEXT_COLOR_YELLOW + "What do you want to promote your pawn to?");
        out.print("Piece Type: " + SET_TEXT_COLOR_WHITE);
        String type = scanner.nextLine().toLowerCase();
        ChessPiece.PieceType newType = switch(type){
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            default -> null;
        };
        if(newType == null)
        {
            out.println(SET_TEXT_COLOR_RED + "That is not a valid piece. Try again");
            return getPromotionPiece();
        }
        return newType;
    }


    private void resign() throws ServerException
    {
        out.println(RESET_TEXT);
        if(observer)
        {
            help();
            return;
        }

        //Do Websocket stuff
    }

    private void leave() throws ServerException
    {


        //Do Websocket stuff
    }

    private Collection<ChessPosition> highlightMoves() throws ServerException
    {
        ChessPosition position;
        HashSet<ChessPosition> moves = new HashSet<>();
        return moves;
    }

    private Collection<ChessPosition> highlight(ChessPosition position)
    {
        HashSet<ChessPosition> moves = new HashSet<>();
        return moves;
    }

    private void printBoard() throws ServerException
    {
        ChessBoard fakeBoard = new ChessBoard();
        fakeBoard.resetBoard();
        out.print(RESET_ALL);
        out.println(SET_BG_COLOR_DARK_GREEN + EMPTY.repeat(10) + SET_BG_COLOR_DARK_GREY);
        if(observer || playerColor == ChessGame.TeamColor.WHITE)
            printBoardWhite(fakeBoard);
        else
            printBoardBlack(fakeBoard);
        out.print(EMPTY);
        out.println(SET_BG_COLOR_DARK_GREY);
        out.println();
        out.println();
    }

    private void printBoardWhite(ChessBoard board)
    {
        for(int r = 8; r > 0; r --)
        {
            boardHelper(r, board);
        }
        out.print(SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_WHITE + EMPTY);
        for(char c = 'a'; c < 'i'; c++)
        {
            out.print(" " + c + " ");
        }
    }

    private void printBoardBlack(ChessBoard board)
    {
        for(int r = 1; r < 9; r ++)
        {
            boardHelper(r, board);
        }
        out.print(SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_WHITE + EMPTY);
        for(char c = 'h'; c >= 'a'; c--)
        {
            out.print(" " + c + " ");
        }
    }
    private void boardHelper(int r, ChessBoard board)
    {
        out.print(SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_WHITE + " " + r + " ");
        for(int c = 1; c < 9; c++)
        {
            ChessPosition position = new ChessPosition(r, c);
            if((r + c) % 2 == 0)
                out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLUE + getPiece(board, position));
            else
                out.print(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLUE + getPiece(board, position));
        }
        out.print(SET_BG_COLOR_DARK_GREEN + EMPTY + SET_BG_COLOR_DARK_GREY + "\n");
    }

    private String getPiece(ChessBoard bored, ChessPosition pos)
    {
        ChessPiece piece = bored.getPiece(pos);
        if(piece == null)
            return EMPTY;
        return switch (piece.getPieceType())
        {
            case PAWN -> (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) ? WHITE_PAWN : BLACK_PAWN;
            case BISHOP -> (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) ? WHITE_ROOK : BLACK_ROOK;
            case QUEEN -> (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) ? WHITE_QUEEN : BLACK_QUEEN;
            case KING -> (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) ? WHITE_KING : BLACK_KING;
        };

    }
}

package ui;

import chess.*;
import model.AuthData;
import websocket.GameHandler;
import websocket.WebSocketFacade;

import java.io.PrintStream;
import java.rmi.ServerException;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_ALL;

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
            observer = true;
        else
            observer = false;
    }

    public void run()
    {
        out.println(ERASE_SCREEN + RESET_TEXT);
        out.println(SET_BG_COLOR_DARK_GREY);
        out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + BLACK_PAWN + "Let the games begin!" + BLACK_PAWN);
        out.println(RESET_TEXT);

        boolean quit = false;
        while(!quit)
        {
            try{
                printBoard();
                String line = scanner.nextLine();
                quit = this.eval(line);

            } catch (Throwable e) {
                var msg = e.toString();
                out.println(msg);
            }
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
                case "leave" -> quitting = true;
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
        ChessMove move;
        ChessPiece piece;


    }

    private void resign() throws ServerException
    {

    }

    private void leave() throws ServerException
    {

    }

    private void highlightMoves() throws ServerException
    {
        ChessPosition position;
    }

    private void printBoard() throws ServerException
    {
        ChessBoard fakeBoard = new ChessBoard();
        fakeBoard.resetBoard();
        out.print(RESET_ALL);
        if(playerColor == ChessGame.TeamColor.WHITE)
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

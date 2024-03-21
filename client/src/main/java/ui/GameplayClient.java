package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;

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
    public GameplayClient(ServerFacade server, String serverURL, AuthData auth, int gameID, ChessGame.TeamColor color, Scanner scanner, PrintStream out)
    {
        this.server = server;
        this.serverUrl = serverURL;
        this.auth = auth;
        this.gameID = gameID;
        this.playerColor = color;
        this.scanner = scanner;
        this.out = out;
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
                default -> help();
            };
        } catch (ServerException ex) {
            System.out.println(ex.getMessage());
        }
        return quitting;
    }
    public void help()
    {
        out.print(RESET_TEXT);
        out.println(SET_TEXT_COLOR_MAGENTA + SET_TEXT_BOLD + "Here's how to play:");
    }

    private void makeMove() throws ServerException
    {

    }

    private void printBoard() throws ServerException
    {
        ChessBoard fakeBoard = new ChessBoard();
        fakeBoard.resetBoard();
        out.print(RESET_ALL);

//        if(playerColor == ChessGame.TeamColor.WHITE)
        out.println(SET_BG_COLOR_DARK_GREEN + EMPTY.repeat(10) + SET_BG_COLOR_DARK_GREY);
        for(int r = 8; r > 0; r --)
        {
            boardHelper(r, fakeBoard);
        }
        out.print(SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_WHITE + EMPTY);
        for(char c = 'a'; c < 'i'; c++)
        {
            out.print(" " + c + " ");
        }
        out.print(EMPTY);
        out.println(SET_BG_COLOR_DARK_GREY);
        out.println();
        out.println();

//        else
        out.println(SET_BG_COLOR_DARK_GREEN + EMPTY.repeat(10) + SET_BG_COLOR_DARK_GREY);
        for(int r = 1; r < 9; r ++)
        {
            boardHelper(r, fakeBoard);
        }
        out.print(SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_WHITE + EMPTY);
        for(char c = 'h'; c >= 'a'; c--)
        {
            out.print(" " + c + " ");
        }
        out.print(EMPTY);
        out.println(SET_BG_COLOR_DARK_GREY);
        out.println();


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

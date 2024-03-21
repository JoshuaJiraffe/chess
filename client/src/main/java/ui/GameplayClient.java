package ui;

import chess.ChessGame;
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
        out.println(ERASE_SCREEN);
        out.println(SET_BG_COLOR_DARK_GREY);
        out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + WHITE_QUEEN + "Welcome to Gameplay Client" + WHITE_QUEEN);
        out.println(RESET_TEXT);
        boolean quit = false;
        this.help();
        while(!quit)
        {
            String line = scanner.nextLine();
            try{
                quit = this.eval(line);

            } catch (Throwable e) {
                var msg = e.toString();
                out.println(msg);
            }

        }
        out.println(RESET_ALL);
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

    public void makeMove() throws ServerException
    {

    }

    public void help()
    {

    }
}

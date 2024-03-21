package ui;

import model.AuthData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_ALL;

public class PostLoginClient
{
    private final ServerFacade server;
    private final String serverUrl;
    Scanner scanner;
    private PrintStream out;
    private AuthData auth;
    public PostLoginClient(ServerFacade server, String serverURL, AuthData auth, Scanner scanner, PrintStream out)
    {
        this.server = server;
        this.serverUrl = serverURL;
        this.auth = auth;
        this.scanner = scanner;
        this.out = out;
    }
    public void run()
    {
        out.println(ERASE_SCREEN);
        out.println(SET_BG_COLOR_DARK_GREY);
        out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + WHITE_ROOK + "You successfully logged in!" + WHITE_ROOK);
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
        out.println(SET_TEXT_COLOR_MAGENTA + SET_TEXT_ITALIC + "You have successfully logged out. Type help to see the available commands" + RESET_TEXT);

    }

    public boolean eval(String input) throws ServerException
    {
        var quitting = false;
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (cmd) {
                case "create" -> createGame();
                case "list" -> listGames();
                case "join" -> joinGame();
                case "observe" -> observeGame();
                case "logout" ->
                {
                    logout();
                    quitting = true;
                }
                default -> help();
            };
        } catch (ServerException ex) {
            System.out.println(ex.getMessage());
        }
        return quitting;
    }

    public void help()
    {

    }

    private void logout() throws ServerException
    {
        server.logout(auth.authToken());
        out.println(ERASE_SCREEN);
    }

    public void createGame() throws ServerException
    {

    }

    public void listGames() throws ServerException
    {

    }

    public void joinGame() throws ServerException
    {

    }

    public void observeGame() throws ServerException
    {

    }


}

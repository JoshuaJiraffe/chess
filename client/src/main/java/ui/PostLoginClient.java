package ui;

import chess.ChessGame;
import model.AuthData;
import model.GameData;


import java.io.PrintStream;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;


public class PostLoginClient
{
    private final ServerFacade server;
    private final String serverUrl;
    Scanner scanner;
    private PrintStream out;
    private AuthData auth;
    private ArrayList<GameData> storedGames;
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
        out.println(RESET_TEXT);
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
                case "join" -> joinGame(false);
                case "observe" -> joinGame(true);
                case "logout" ->
                {
                    logout();
                    quitting = true;
                }
                default -> help();
            };
        } catch (ServerException ex) {
            System.out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + ex.getMessage());
        }
        return quitting;
    }

    public void help()
    {
        out.print(RESET_TEXT);
        out.println(SET_TEXT_COLOR_MAGENTA + SET_TEXT_BOLD + "Here are the available commands:\n" + RESET_TEXT_BOLD_FAINT +
                SET_TEXT_COLOR_YELLOW + "create " + SET_TEXT_COLOR_BLUE + "- make a new chess game\n" +
                SET_TEXT_COLOR_YELLOW + "list " + SET_TEXT_COLOR_BLUE + "- see all existing chess games\n" +
                SET_TEXT_COLOR_YELLOW + "join " + SET_TEXT_COLOR_BLUE + "- play in an existing chess game\n" +
                SET_TEXT_COLOR_YELLOW + "observe " + SET_TEXT_COLOR_BLUE + "- observe an ongoing chess game\n" +
                SET_TEXT_COLOR_YELLOW + "help " + SET_TEXT_COLOR_BLUE + "- see available commands\n" +
                SET_TEXT_COLOR_YELLOW + "logout " + SET_TEXT_COLOR_BLUE + "- give up once you've had enough of losing"
        );
        out.println(RESET_TEXT);
    }

    private void logout() throws ServerException
    {
        server.logout(auth.authToken());
        out.println(ERASE_SCREEN);
    }

    public void createGame() throws ServerException
    {
        out.print(RESET_TEXT);
        out.println(SET_TEXT_COLOR_MAGENTA + "Let's get a new game up and rolling!");
        out.println();
        try
        {
            out.print(SET_TEXT_COLOR_YELLOW + "What shall we name this game? " + SET_TEXT_COLOR_WHITE);
            String gameName = scanner.nextLine();
            server.createGame(auth.authToken(), gameName);
            out.println(SET_TEXT_COLOR_MAGENTA + gameName + " was successfully created");
        } catch(ServerException ex)
        {
            String msg = ex.getMessage();
            try {
                int code = Integer.parseInt(msg.substring(msg.length() - 3));
                if(code == 401)
                    out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + "We have reason to believe that you're an imposter. Try logging out and logging back in again");
                else if(code == 400)
                    out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + "Sorry, a game with that name already exists. Try again");
                else
                    throw ex;
            } catch(NumberFormatException e){
                throw ex;
            }
        }
        out.println(RESET_TEXT);
    }

    public void listGames() throws ServerException
    {
        out.print(RESET_TEXT);
        out.println(SET_TEXT_COLOR_MAGENTA + "Here are all the ongoing games:");
        out.println();
        try
        {
            ArrayList<GameData> games = (ArrayList<GameData>) server.listGames(auth.authToken());
            printGames(games);
            storedGames = games;

        } catch(ServerException ex)
        {
            String msg = ex.getMessage();
            try {
                int code = Integer.parseInt(msg.substring(msg.length() - 3));
                if(code == 401)
                    out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + "We have reason to believe that you're an imposter. Try logging out and logging back in again");
                else
                    throw ex;
            } catch(NumberFormatException e){
                throw ex;
            }
        }
        out.println(RESET_TEXT);
    }

    private void printGames(ArrayList<GameData> games)
    {
        int pad = 20;
        out.print(SET_TEXT_COLOR_YELLOW + SET_TEXT_BOLD + padRight("#:", 5));
        out.print(padRight("Game Name:", pad));
        out.print(padRight("White Username:", pad));
        out.print(padRight("Black Username:", pad));
        out.println(SET_TEXT_COLOR_BLUE + RESET_TEXT_BOLD_FAINT);
        for(int i = 0; i < games.size(); i++)
        {
            GameData game = games.get(i);
            out.println(padRight(Integer.toString(i), 5) + padRight(game.gameName(), pad) + padRight(game.whiteUsername(), pad) + padRight(game.blackUsername(), pad));
        }
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public void joinGame(boolean spectator) throws ServerException
    {
        out.print(RESET_TEXT);
        if(storedGames == null)
        {
            out.println(SET_TEXT_COLOR_RED + "You have to look at the available games first, silly");
            return;
        }
        if(spectator)
            out.println(SET_TEXT_COLOR_MAGENTA + "Ready to watch some amazing, top-level chess gameplay?");
        else
            out.println(SET_TEXT_COLOR_MAGENTA + "Hoho, you think you're ready for a real game of chess? We'll see about that");
        out.println();
        try
        {
            out.println(SET_TEXT_COLOR_YELLOW + "Pick the game you want to join using its number from the list");
            out.print(SET_TEXT_COLOR_YELLOW + "Game Number: " + SET_TEXT_COLOR_WHITE);
            String id = scanner.nextLine();
            while(!isInteger(id))
            {
                out.println(SET_TEXT_COLOR_RED + "The game number must be a number");
                out.print(SET_TEXT_COLOR_YELLOW + "Game Number: " + SET_TEXT_COLOR_WHITE);
                id = scanner.nextLine();
            }
            int numID = Integer.parseInt(id);
            if(numID < 0 || numID >= storedGames.size())
            {
                out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + "There is no game with that number" + RESET_TEXT_ITALIC);
                return;
            }
            int realID = storedGames.get(numID).gameID();
            ChessGame.TeamColor teamColor = null;
            if(!spectator)
            {
                String color = "";
                out.println(SET_TEXT_COLOR_YELLOW + "Which color do you want to be");
                while(!color.equals("white") && !color.equals("black"))
                {
                    out.print(SET_TEXT_COLOR_YELLOW + "White/Black" + SET_TEXT_COLOR_WHITE);
                    color = scanner.nextLine().toLowerCase();
                    if(!color.equals("white") && !color.equals("black"))
                        out.println(SET_TEXT_COLOR_RED + "That is not a valid color");
                }
                teamColor = (color.equals("white")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            }
            server.joinGame(auth.authToken(), teamColor, realID);
            out.println();
            out.println(SET_TEXT_COLOR_MAGENTA + SET_TEXT_ITALIC + "Joining game " + id);
            new GameplayClient(server, serverUrl, auth, realID, teamColor, scanner, out).run();

        } catch(ServerException ex)
        {
            String msg = ex.getMessage();
            try {
                int code = Integer.parseInt(msg.substring(msg.length() - 3));
                if(code == 401)
                    out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + "We have reason to believe that you're an imposter. Try logging out and logging back in again");
                else if (code == 400)
                    out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + "There is no game with that id. Try again");
                else if (code == 403)
                    out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + "Someone is already playing in this game as that color. Convince them to leave and maybe you can have a go");
                else
                    throw ex;
            } catch(NumberFormatException e){
                throw ex;
            }
        }
        out.println(RESET_TEXT);
    }

    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}

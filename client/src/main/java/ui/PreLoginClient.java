package ui;

import model.AuthData;
import model.UserData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerException;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginClient
{
    private final ServerFacade server;
    private final String serverUrl;
    Scanner scanner;
    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public PreLoginClient(String serverUrl)
    {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
        scanner = new Scanner(System.in);
    }

    public void run()
    {
        out.println(ERASE_SCREEN);
        out.println(RESET_TEXT);
        out.println(SET_BG_COLOR_DARK_GREY);
        out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + WHITE_QUEEN + "Welcome to Jiraffe's amazing chess game. Ready to lose?" + WHITE_QUEEN);
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
        scanner.close();
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
                case "register" -> register();
                case "login" -> login();
                case "quit" ->
                {
                    quit();
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
                SET_TEXT_COLOR_YELLOW + "register " + SET_TEXT_COLOR_BLUE + "- create a new account to play chess\n" +
                SET_TEXT_COLOR_YELLOW + "login " + SET_TEXT_COLOR_BLUE + "- login to an existing account to play chess\n" +
                SET_TEXT_COLOR_YELLOW + "help " + SET_TEXT_COLOR_BLUE + "- see available commands\n" +
                SET_TEXT_COLOR_YELLOW + "quit " + SET_TEXT_COLOR_BLUE + "- give up once you've had enough of losing"
        );
        out.println(RESET_TEXT);
    }

    private void quit() throws ServerException
    {
        out.print(RESET_TEXT);
        out.println(ERASE_SCREEN);
        out.println(SET_TEXT_COLOR_MAGENTA + "Goodbye, thanks for playing");
    }

    private void login() throws ServerException
    {
        out.print(RESET_TEXT);
        out.println(SET_TEXT_COLOR_MAGENTA + "Alright, let's make sure you're actually legit");
        out.println();
        try
        {
            out.print(SET_TEXT_COLOR_YELLOW + "Username: " + SET_TEXT_COLOR_WHITE);
            String username = scanner.nextLine();
            out.print(SET_TEXT_COLOR_YELLOW + "Password: " + SET_TEXT_COLOR_WHITE);
            String password = scanner.nextLine();
            AuthData auth = server.login(username, password);
            new PostLoginClient(server, serverUrl, auth, scanner, out).run();
        } catch(ServerException ex)
        {
            String msg = ex.getMessage();
            try {
                int code = Integer.parseInt(msg.substring(msg.length() - 3));
                if(code == 401)
                    out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + "Sorry, those credentials are INVALID. Try again");
                else
                    throw ex;
            } catch(NumberFormatException e){
                throw ex;
            }
        }
        out.println(RESET_TEXT);
    }

    private void register() throws ServerException
    {
        out.print(RESET_TEXT);
        out.println(SET_TEXT_COLOR_MAGENTA + "So you want to make an account now do you? I'm gonna need some info first");
        out.println();
        try{
            out.print(SET_TEXT_COLOR_YELLOW + "Email: " + SET_TEXT_COLOR_WHITE);
            String email = scanner.nextLine();
            out.print(SET_TEXT_COLOR_YELLOW + "Username: " + SET_TEXT_COLOR_WHITE);
            String username = scanner.nextLine();
            while(username.equals("null") || username.length() > 20)
            {
                if(username.equals("null"))
                    out.print(SET_TEXT_COLOR_RED + "Nice try. No null names allowed");
                if(username.length() > 20)
                    out.print(SET_TEXT_COLOR_RED + "Sorry. That username is too long");
                out.print(SET_TEXT_COLOR_YELLOW + "Username: " + SET_TEXT_COLOR_WHITE);
                username = scanner.nextLine();
            }
            out.print(SET_TEXT_COLOR_YELLOW + "Password: " + SET_TEXT_COLOR_WHITE);
            String password = scanner.nextLine();
            UserData user = new UserData(username, password, email);
            AuthData auth = server.register(user);
            new PostLoginClient(server, serverUrl, auth, scanner, out).run();

        } catch(ServerException ex){
            String msg = ex.getMessage();
            try {
                int code = Integer.parseInt(msg.substring(msg.length() - 3));
                if(code == 403)
                    out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + "Sorry, that username or email is already taken. Try again");
                else if(code == 400)
                    out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + "All fields require values. Try again");
                else
                    throw ex;
            } catch(NumberFormatException e){
                throw ex;
            }
        }
        out.println(RESET_TEXT);
    }
}



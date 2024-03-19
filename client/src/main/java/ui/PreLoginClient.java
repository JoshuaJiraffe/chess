package ui;

import java.rmi.ServerException;
import java.util.Arrays;

public class PreLoginClient
{
    private final ServerFacade server;
    private final String serverUrl;

    public PreLoginClient(String serverUrl)
    {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
    }

    public void run()
    {

    }

    public void eval(String input) throws ServerException
    {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (cmd) {
                case "register" -> register();
                case "login" -> login();
                case "quit" -> quit();
                default -> help();
            };
        } catch (ServerException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void help() throws ServerException
    {

    }

    public void quit() throws ServerException
    {

    }

    public void login() throws ServerException
    {

    }

    public void register() throws ServerException
    {

    }
}

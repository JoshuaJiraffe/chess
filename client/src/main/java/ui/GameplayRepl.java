package ui;

import chess.ChessGame;
import model.AuthData;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import websocket.GameHandler;
import websocket.WebSocketFacade;

import java.io.PrintStream;
import java.rmi.ServerException;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT;

public class GameplayRepl implements GameHandler
{
    private final ServerFacade server;
    private final String serverUrl;
    Scanner scanner;
    private PrintStream out;
    private AuthData auth;
    private int gameID;
    private ChessGame.TeamColor playerColor;
    private final WebSocketFacade ws;
    private ChessGame game;
    private GameplayClient gameClient;

    public GameplayRepl(ServerFacade server, String serverURL, AuthData auth, int gameID, ChessGame.TeamColor color, Scanner scanner, PrintStream out) throws ServerException
    {
        this.server = server;
        this.serverUrl = serverURL;
        this.auth = auth;
        this.gameID = gameID;
        this.playerColor = color;
        this.scanner = scanner;
        this.out = out;
        ws = new WebSocketFacade(serverURL, this);
        if(color == null)
            ws.joinObserver(auth.authToken(), gameID, auth.username());
        else
            ws.joinPlayer(auth.authToken(), gameID, playerColor);
        this.gameClient = new GameplayClient(server, serverURL, auth, gameID, color, scanner, out, this, ws);
    }

    public void run()
    {
        out.println(ERASE_SCREEN + RESET_TEXT);
        out.println(SET_BG_COLOR_DARK_GREY);
        out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + BLACK_PAWN + "Let the game begin!" + BLACK_PAWN);
        out.println(RESET_TEXT);
        try
        {
            boolean quit = false;
            while(!quit)
            {
                String line = scanner.nextLine();
                quit = gameClient.eval(line);
            }
        } catch (Throwable e) {
            var msg = e.toString();
            out.println(msg);
        }
        out.println(SET_TEXT_COLOR_MAGENTA + SET_TEXT_ITALIC + "You lost. Or maybe you won. Who knows?" + RESET_TEXT);
    }




    @Override
    public void updateGame(LoadGameMessage message)
    {
        ChessGame newGame = message.getGame();
        out.println(SET_TEXT_ITALIC + SET_TEXT_COLOR_GREEN + message.getMessage() + RESET_TEXT);
        gameClient.redrawBoard(newGame);

    }

    @Override
    public void printMessage(ServerMessage message)
    {
//        Notifications
        if(message.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION)
        {
            NotificationMessage notMess = (NotificationMessage) message;
            out.println(SET_TEXT_ITALIC + SET_TEXT_COLOR_GREEN + notMess.getMessage() + RESET_TEXT);
        }
//            Errors
        else
        {
            ErrorMessage errMess = (ErrorMessage) message;
            out.println(SET_TEXT_COLOR_RED + SET_TEXT_ITALIC + ((ErrorMessage) message).getErrorMessage() + RESET_TEXT);
        }
    }
}

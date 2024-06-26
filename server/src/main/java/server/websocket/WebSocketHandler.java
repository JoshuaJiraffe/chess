package server.websocket;

import chess.ChessGame;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@WebSocket
public class WebSocketHandler
{
    private final GameService gameService;
    private final WebSocketSessions sessions;
    public WebSocketHandler(GameService service)
    {
        gameService = service;
        sessions = new WebSocketSessions();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {}

    @OnWebSocketClose
    public void onClose(Session session, int num, String str) {}

    @OnWebSocketError
    public void onError(Throwable throwable) {}

    @OnWebSocketMessage
    public void onMessage(Session session, String str) throws IOException
    {
        UserGameCommand command = new Gson().fromJson(str, UserGameCommand.class);
        switch (command.getCommandType())
        {
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(str, JoinPlayerCommand.class), session);
            case JOIN_OBSERVER -> joinObserver(new Gson().fromJson(str, JoinObserverCommand.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(str, MakeMoveCommand.class));
            case LEAVE -> leaveGame(new Gson().fromJson(str, LeaveCommand.class), session);
            case RESIGN -> resignGame(new Gson().fromJson(str, ResignCommand.class));
        }
    }

    private void joinPlayer(JoinPlayerCommand cmd, Session session) throws IOException
    {
        try {
            sessions.addSessionToGame(cmd.getGameID(), cmd.getAuthString(), session);
            GameData gameData = gameService.getGame(cmd.getGameID());
            AuthData authData = gameService.getAuth(cmd.getAuthString());
            String username;
            if(cmd.getPlayerColor() == ChessGame.TeamColor.WHITE)
                username = gameData.whiteUsername();
            else
                username = gameData.blackUsername();
            if((username == null) ||!username.equals(authData.username()))
            {
                sendMessage(cmd.getGameID(), new ErrorMessage("Error, unauthorized"), cmd.getAuthString());
                sessions.removeSession(session);
                return;
            }
            String sendMessage = "You have successfully joined the game \'" + gameData.gameName() + "\'";
            sendMessage(cmd.getGameID(), new LoadGameMessage(gameData.game(), sendMessage), cmd.getAuthString());
            String broadMessage = username + " has joined the game as " + cmd.getPlayerColor().toString().toLowerCase();
            broadcastMessage(gameData.gameID(), new NotificationMessage(broadMessage), cmd.getAuthString());
        } catch (DataAccessException ex) {
            sendMessage(cmd.getGameID(), new ErrorMessage("Error " + ex.getMessage()), cmd.getAuthString());
            sessions.removeSession(session);
        }

    }

    private void joinObserver(JoinObserverCommand cmd, Session session) throws IOException
    {
        try {
            sessions.addSessionToGame(cmd.getGameID(), cmd.getAuthString(), session);
            GameData gameData = gameService.getGame(cmd.getGameID());
            AuthData authData = gameService.getAuth(cmd.getAuthString());
            String sendMessage = "You have successfully joined the game \'" + gameData.gameName() + "\'";
            sendMessage(cmd.getGameID(), new LoadGameMessage(gameData.game(), sendMessage), cmd.getAuthString());
            String broadMessage = cmd.getUser() + " has joined the game as an observer";
            broadcastMessage(gameData.gameID(), new NotificationMessage(broadMessage), cmd.getAuthString());
        } catch (DataAccessException ex) {
            sendMessage(cmd.getGameID(), new ErrorMessage("Error " + ex.getMessage()), cmd.getAuthString());
            sessions.removeSession(session);
        }
    }

    private void makeMove(MakeMoveCommand cmd) throws IOException
    {
        try {
            GameData gameData = gameService.getGame(cmd.getGameID());
            AuthData authData = gameService.getAuth(cmd.getAuthString());
            if(gameData.game().isGameOver())
            {
                sendMessage(cmd.getGameID(), new ErrorMessage("Error: game already over. No more moves can be made"), cmd.getAuthString());
                return;
            }
            String username = authData.username();
            ChessGame.TeamColor colorPlayer;
            if(username.equals(gameData.whiteUsername()))
                colorPlayer = ChessGame.TeamColor.WHITE;
            else if(username.equals(gameData.blackUsername()))
                colorPlayer = ChessGame.TeamColor.BLACK;
            else
            {
                sendMessage(cmd.getGameID(), new ErrorMessage("Error, unauthorized"), cmd.getAuthString());
                return;
            }
            ChessGame.TeamColor colorMove = gameData.game().getBoard().getPiece(cmd.getMove().getStartPosition()).getTeamColor();
            if(colorPlayer != colorMove)
            {
                sendMessage(cmd.getGameID(), new ErrorMessage("Error, you can't move other peoples pieces"), cmd.getAuthString());
                return;
            }

            ChessGame updatedGame = gameData.game();
            ChessPiece piece = updatedGame.getBoard().getPiece(cmd.getMove().getStartPosition());
            updatedGame.makeMove(cmd.getMove());
            ChessGame.TeamColor nextTurn = updatedGame.getTeamTurn();
            String nextUser = (nextTurn == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
            if(updatedGame.isInCheckmate(nextTurn))
            {
                updatedGame.endGame();
                updatedGame.setWinner((nextTurn == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);
            }
            else if(updatedGame.isInStalemate(nextTurn))
                updatedGame.endGame();
            gameService.updateGame(cmd.getGameID(), updatedGame, null);

            broadcastMessage(gameData.gameID(), new LoadGameMessage(updatedGame, ""), null);
            String broadMessage = cmd.getUser() + " played " + piece.getPieceType().toString().toLowerCase() + cmd.getMove().toString();
            broadcastMessage(gameData.gameID(), new NotificationMessage(broadMessage), cmd.getAuthString());

            if(updatedGame.isGameOver())
            {
                ChessGame.TeamColor winner = updatedGame.getWinner();
                if(winner == null)
                    broadMessage = "The game is over! It ended in a stalemate, how lame";
                else
                    broadMessage = nextUser + " has been checkmated, which means we have our champion. Congratulations to " + cmd.getUser();
                broadcastMessage(gameData.gameID(), new NotificationMessage(broadMessage), null);
            }
            else if(updatedGame.isInCheck(nextTurn))
            {
                broadMessage = nextUser + " is in check!";
                broadcastMessage(gameData.gameID(), new NotificationMessage(broadMessage), null);
            }

        } catch (DataAccessException | InvalidMoveException ex) {
            sendMessage(cmd.getGameID(), new ErrorMessage("Error " + ex.getMessage()), cmd.getAuthString());
        }

    }

    private void leaveGame(LeaveCommand cmd, Session session) throws IOException
    {
        try {
            String user = cmd.getUser();
            GameData gameData = gameService.getGame(cmd.getGameID());
            if(gameData.whiteUsername().equals(user))
                gameService.updateGame(cmd.getGameID(), gameData.game(), ChessGame.TeamColor.WHITE);
            else if(gameData.blackUsername().equals(user))
                gameService.updateGame(cmd.getGameID(), gameData.game(), ChessGame.TeamColor.BLACK);
            String broadMessage = user + " has left the game. Coward";
            broadcastMessage(gameData.gameID(), new NotificationMessage(broadMessage), cmd.getAuthString());
            sessions.removeSession(session);
        } catch (DataAccessException ex) {
            sendMessage(cmd.getGameID(), new ErrorMessage("Error " + ex.getMessage()), cmd.getAuthString());
        }
    }

    private void resignGame(ResignCommand cmd) throws IOException
    {
        try {
            GameData gameData = gameService.getGame(cmd.getGameID());
            AuthData authData = gameService.getAuth(cmd.getAuthString());
            String user = authData.username();
            ChessGame game = gameData.game();
            if(game.isGameOver())
            {
                sendMessage(cmd.getGameID(), new ErrorMessage("Error: game already over"), cmd.getAuthString());
                return;
            }
            if(gameData.blackUsername().equals(user))
            {
                game.endGame();
                game.setWinner(ChessGame.TeamColor.WHITE);
            }
            else if (gameData.whiteUsername().equals(user))
            {
                game.endGame();
                game.setWinner(ChessGame.TeamColor.BLACK);
            }
            else
            {
                sendMessage(cmd.getGameID(), new ErrorMessage("Error: you can't resign if you're not playing"), cmd.getAuthString());
                return;
            }
            gameService.updateGame(cmd.getGameID(), game, null);
            String broadMessage = user + " has resigned. What a loser. " + game.getWinner().toString().toLowerCase() + " has won!";
            broadcastMessage(gameData.gameID(), new NotificationMessage(broadMessage), null);
        } catch (DataAccessException ex) {
            sendMessage(cmd.getGameID(), new ErrorMessage("Error " + ex.getMessage()), cmd.getAuthString());
        }
    }

    public void sendMessage(int gameID, ServerMessage message, String authToken) throws IOException
    {
        HashMap<String, Session> gameSessions = sessions.getSessionsForGame(gameID);
        var s = gameSessions.get(authToken);
        if(s.isOpen())
            s.getRemote().sendString(new Gson().toJson(message));
        else
            sessions.removeSession(s);
    }

    public void broadcastMessage(int gameID, ServerMessage message, String exceptThisAuthToken) throws IOException
    {
        var removeList = new ArrayList<Session>();
        HashMap<String, Session> gameSessions = sessions.getSessionsForGame(gameID);
        for (String auth : gameSessions.keySet()) {
            var s = gameSessions.get(auth);
            if (s.isOpen()) {
                if (!auth.equals(exceptThisAuthToken)) {
                    s.getRemote().sendString(new Gson().toJson(message));
                }
            } else {
                removeList.add(s);
            }
        }

        // Clean up any connections that were left open.
        for (var s : removeList) {
            sessions.removeSession(s);
        }
    }

}

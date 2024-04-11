package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.ServerException;

public class WebSocketFacade extends Endpoint
{
    Session session;
    private GameHandler gameHand;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
    public void onClose() {}
    public void onError() {}

    public WebSocketFacade(String url, GameHandler gameHandler) throws ServerException
    {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.gameHand = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION -> gameHand.printMessage(new Gson().fromJson(message, NotificationMessage.class));
                        case ERROR -> gameHand.printMessage(new Gson().fromJson(message, ErrorMessage.class));
                        case LOAD_GAME -> gameHand.updateGame(new Gson().fromJson(message, LoadGameMessage.class));
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ServerException(ex.getMessage());
        }
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor playerColor) throws ServerException
    {
        try {
            var command = new JoinPlayerCommand(authToken, gameID, playerColor);
            System.out.println("we in client web socket facade");
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ServerException(ex.getMessage());
        }
    }
    public void joinObserver(String authToken, int gameID, String user) throws ServerException
    {
        try {
            var command = new JoinObserverCommand(authToken, gameID, user);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ServerException(ex.getMessage());
        }
    }
    public void makeMove(String authToken, int gameID, String user, ChessMove move) throws ServerException
    {
        try {
            var command = new MakeMoveCommand(authToken, gameID, user, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ServerException(ex.getMessage());
        }
    }
    public void leaveGame(String authToken, int gameID, String user) throws ServerException
    {
        try {
            var command = new LeaveCommand(authToken, gameID, user);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            this.session.close();
        } catch (IOException ex) {
            throw new ServerException(ex.getMessage());
        }
    }
    public void resignGame(String authToken, int gameID, String user) throws ServerException
    {
        try {
            var command = new ResignCommand(authToken, gameID, user);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ServerException(ex.getMessage());
        }
    }


}

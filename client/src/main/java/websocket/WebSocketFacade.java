package websocket;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

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
                    gameHand.printMessage(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ServerException(ex.getMessage());
        }
    }

    public void joinPlayer(String authToken) throws ServerException
    {
        try {
            var action = new UserGameCommand(authToken);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ServerException(ex.getMessage());
        }
    }
    public void joinObserver() throws ServerException
    {

    }
    public void makeMove() throws ServerException
    {

    }
    public void leaveGame() throws ServerException
    {

    }
    public void resignGame() throws ServerException
    {

    }


}

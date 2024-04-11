package websocket;

import chess.ChessGame;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

import java.rmi.ServerException;

public interface GameHandler
{
    public void updateGame(LoadGameMessage message);
    public void printMessage(ServerMessage message);
}

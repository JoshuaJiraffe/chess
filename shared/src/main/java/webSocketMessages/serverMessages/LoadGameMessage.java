package webSocketMessages.serverMessages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage
{
    private final ChessGame game;
    private final String message;

    public LoadGameMessage(ChessGame game, String message)
    {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.message = message;
    }

    public ChessGame getGame()
    {
        return game;
    }

    public String getMessage()
    {
        return message;
    }
}

package webSocketMessages.userCommands;

import chess.ChessMove;
import chess.ChessPiece;

public class MakeMoveCommand extends UserGameCommand
{
    private final int gameID;
    private final ChessMove move;
    private final String user;
    public MakeMoveCommand(String authToken, int gameID, String user, ChessMove move)
    {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
        this.user = user;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public int getGameID()
    {
        return gameID;
    }

    public ChessMove getMove()
    {
        return move;
    }

    public String getUser()
    {
        return user;
    }
}

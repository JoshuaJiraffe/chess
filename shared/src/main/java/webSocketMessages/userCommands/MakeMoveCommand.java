package webSocketMessages.userCommands;

import chess.ChessMove;
import chess.ChessPiece;

public class MakeMoveCommand extends UserGameCommand
{
    private final int gameID;
    private final ChessMove move;

    public MakeMoveCommand(String authToken, int gameID, ChessMove move)
    {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
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
}

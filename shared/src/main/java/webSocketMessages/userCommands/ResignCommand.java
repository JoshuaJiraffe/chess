package webSocketMessages.userCommands;

import chess.ChessGame;

public class ResignCommand extends UserGameCommand
{
    private final int gameID;
    private final String user;
    public ResignCommand(String authToken, int gameID, String user)
    {
        super(authToken);
        this.gameID = gameID;
        this.user = user;
        this.commandType = CommandType.RESIGN;
    }

    public int getGameID()
    {
        return gameID;
    }

    public String getUser()
    {
        return user;
    }
}

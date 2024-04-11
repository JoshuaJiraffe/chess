package webSocketMessages.userCommands;

import chess.ChessGame;

public class ResignCommand extends UserGameCommand
{
    private final int gameID;
    private final String user;
    private final ChessGame.TeamColor color;
    public ResignCommand(String authToken, int gameID, String user, ChessGame.TeamColor color)
    {
        super(authToken);
        this.gameID = gameID;
        this.user = user;
        this.color = color;
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

    public ChessGame.TeamColor getColor()
    {
        return color;
    }
}

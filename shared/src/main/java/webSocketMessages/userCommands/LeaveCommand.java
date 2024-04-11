package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand
{
    private final int gameID;
    private final String user;

    public LeaveCommand(String authToken, int gameID, String user)
    {
        super(authToken);
        this.gameID = gameID;
        this.user = user;
        this.commandType = CommandType.LEAVE;
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

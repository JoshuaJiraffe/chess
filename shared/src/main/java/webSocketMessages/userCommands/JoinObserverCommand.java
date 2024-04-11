package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand
{
    private final int gameID;
    private final String user;

    public JoinObserverCommand(String authToken, int gameID, String user)
    {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_OBSERVER;
        this.user = user;
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

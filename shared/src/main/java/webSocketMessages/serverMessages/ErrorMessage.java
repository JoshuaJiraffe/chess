package webSocketMessages.serverMessages;

public class ErrorMessage extends ServerMessage
{
    private final String errorMessage;

    public ErrorMessage(ServerMessageType type, String error)
    {
        super(type);
        this.errorMessage = error;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }
}

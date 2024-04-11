package server.websocket;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;

public class WebSocketSessions
{
    public ConcurrentHashMap<Integer, HashMap<AuthData, Session>> sessions = new ConcurrentHashMap<>();

    public void addSessionToGame(int gameID, AuthData authToken, Session session)
    {
        sessions.get(gameID).put(authToken, session);
    }

    public void removeSessionFromGame(int gameID, AuthData authToken, Session session)
    {
        sessions.get(gameID).remove(authToken);
    }

    public void removeSession(Session session)
    {
        for (Map.Entry<Integer, HashMap<AuthData, Session>> entry : sessions.entrySet())
        {
            HashMap<AuthData, Session> innerMap = entry.getValue();
            // Iterate over the inner HashMap associated with each key
            for (Map.Entry<AuthData, Session> innerEntry : innerMap.entrySet())
            {
                if (innerEntry.getValue().equals(session))
                {
                    // Found the session, remove it from the inner HashMap
                    innerMap.remove(innerEntry.getKey());
                    return; // Exit after removing the session
                }
            }
        }
    }

    public Map<AuthData, Session> getSessionsForGame(int gameID)
    {
        return sessions.get(gameID);
    }

}

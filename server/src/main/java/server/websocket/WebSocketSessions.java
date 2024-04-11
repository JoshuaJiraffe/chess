package server.websocket;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;

public class WebSocketSessions
{
    public ConcurrentHashMap<Integer, HashMap<String, Session>> sessions = new ConcurrentHashMap<>();

    public void addSessionToGame(int gameID, String authToken, Session session)
    {
        if(!sessions.containsKey(gameID))
            sessions.put(gameID, new HashMap<String, Session>());
        sessions.get(gameID).put(authToken, session);
    }

    public void removeSessionFromGame(int gameID, String authToken, Session session)
    {
        sessions.get(gameID).remove(authToken);
        session.close();
    }

    public void removeSession(Session session)
    {
        for (Map.Entry<Integer, HashMap<String, Session>> entry : sessions.entrySet())
        {
            HashMap<String, Session> innerMap = entry.getValue();
            // Iterate over the inner HashMap associated with each key
            for (Map.Entry<String, Session> innerEntry : innerMap.entrySet())
            {
                if (innerEntry.getValue().equals(session))
                {
                    // Found the session, remove it from the inner HashMap
                    innerMap.remove(innerEntry.getKey());
                    session.close();
                    return; // Exit after removing the session
                }
            }
        }
    }

    public HashMap<String, Session> getSessionsForGame(int gameID)
    {
        return sessions.get(gameID);
    }

}

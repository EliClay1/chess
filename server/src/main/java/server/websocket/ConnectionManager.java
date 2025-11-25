package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void addSession(Session session) {
        connections.put(session, session);
    }

    public void removeSession(Session session) {
        connections.remove(session);
    }
}

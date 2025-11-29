package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ConnectionManager {
    public final HashMap<Integer, Set<Session>> connections = new HashMap<>();

    public void addSessionToGame(int gameId, Session session) {
        connections.computeIfAbsent(gameId, id -> Collections.synchronizedSet(new HashSet<>()));
        connections.get(gameId).add(session);
    }

    public void removeSessionFromGame(int gameId, Session session) {
        Set<Session> setOfConnections = connections.get(gameId);
        if (setOfConnections != null) {
            synchronized (setOfConnections) {
                setOfConnections.remove(session);
                if (setOfConnections.isEmpty()) {
                    connections.remove(gameId);
                }
            }
        }
    }

    public Set<Session> getSessionsForGame(int gameId) {
        return connections.getOrDefault(gameId, Collections.emptySet());
    }
}

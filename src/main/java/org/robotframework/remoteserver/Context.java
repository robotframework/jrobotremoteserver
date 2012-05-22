package org.robotframework.remoteserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

    private static final ThreadLocal<Integer> port = new ThreadLocal<Integer>();

    private static final ConcurrentHashMap<Integer, RemoteServer> remoteServerMap = new ConcurrentHashMap<Integer, RemoteServer>();

    protected static void setPort(Integer rPort) {
	port.set(rPort);
    }

    protected static RemoteServer getRemoteServer() {
	return remoteServerMap.get(port.get());
    }

    protected static void addRemoteServer(RemoteServer server, Set<Integer> ports) {
	synchronized (remoteServerMap) {
	    for (Integer port : ports) {
		if (remoteServerMap.containsKey(port))
		    throw new RuntimeException(String.format("Another RemoteServer has claimed port %d", port));
	    }
	    for (Integer port : ports) {
		remoteServerMap.put(port, server);
	    }
	}
    }

    protected static void removeRemoteServer(RemoteServer server) {
	List<Integer> remove = new ArrayList<Integer>();
	for (Integer port : remoteServerMap.keySet())
	    if (remoteServerMap.get(port) == server)
		remove.add(port);
	for (Integer port : remove)
	    remoteServerMap.remove(port);
    }

    protected static IRemoteLibrary getLibrary() {
	return getRemoteServer().getLibrary(port.get());
    }

}

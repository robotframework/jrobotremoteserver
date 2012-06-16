package org.robotframework.remoteserver;

import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ContextTest {
    @Test
    public void portInUse() {
	RemoteServer server = new RemoteServer();
	Set<Integer> ports =  new HashSet<Integer>();
	ports.add(4);
	Context.addRemoteServer(server, ports);
	String msg = null;
	try {
	    Context.addRemoteServer(server, ports);
	} catch (RuntimeException e) {
	    msg = e.getMessage();
	}
	Assert.assertEquals(msg, "Another RemoteServer has claimed port 4");
	Context.removeRemoteServer(server);
    }

    @Test
    public void portFreed() {
	RemoteServer server = new RemoteServer();
	Set<Integer> ports =  new HashSet<Integer>();
	ports.add(5);
	Context.addRemoteServer(server, ports);
	Context.removeRemoteServer(server);
	Context.addRemoteServer(server, ports);
	Context.removeRemoteServer(server);
    }
}

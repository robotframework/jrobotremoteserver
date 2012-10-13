package org.robotframework.remoteserver.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.robotframework.remoteserver.RemoteServer;
import org.robotframework.remoteserver.testlibraries.StaticOne;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RemoteServerServletTest {

    @Test
    public void serverClosesHttp10Connections() throws Exception {
	Socket s = new Socket((String)null, 8270);

	PrintWriter writer = new PrintWriter(s.getOutputStream ());
	BufferedReader reader = new BufferedReader (new InputStreamReader(s.getInputStream ()));

	String head = "POST / HTTP/1.0\r\nUser-Agent: test\r\nHost: localhost\r\nContent-Type: text/xml\r\nContent-length: ";
	writer.print(head);
	writer.print(Integer.toString(body.length()));
	writer.print("\r\n\r\n");
	writer.print(body);
	writer.flush();

	StringBuilder output = new StringBuilder();
	String inputLine;
	while ((inputLine = reader.readLine()) != null) 
	    output.append(inputLine + "\n");
	Assert.assertTrue(output.toString().contains("stop_remote_server"));
        boolean closed = false;
        try { // should fail on first write when in CLOSE_WAIT but doesn't
	    for(int i=0; i<100; i++) {
	        s.getOutputStream().write(5);
	        s.getOutputStream().flush();
	    }
	} catch (SocketException e) {
	    closed = true;
	}
        Assert.assertTrue(closed);
	reader.close();
	writer.close();
	s.close();
    }

    @SuppressWarnings("unused")
    @BeforeClass
    private void startServer() throws Exception {
	server = new RemoteServer();
	server.addLibrary(StaticOne.class, 8270);
	server.start();
    }

    @SuppressWarnings("unused")
    @AfterClass
    private void stopServer() throws Exception {
	server.stop();
    }

    private RemoteServer server;
    private final String body = "<?xml version=\"1.0\"?><methodCall><methodName>get_keyword_names</methodName><params /></methodCall>";
}

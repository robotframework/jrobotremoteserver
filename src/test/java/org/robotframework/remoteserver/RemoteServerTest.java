package org.robotframework.remoteserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.robotframework.remoteserver.testlibraries.StaticOne;
import org.robotframework.remoteserver.testlibraries.StaticTwo;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class RemoteServerTest {
    RemoteServer server;

    @Test
    public void allowRemoteStop() throws Exception {
	server = new RemoteServer();
	Assert.assertEquals(server.getAllowStop(), true);
	server.setAllowStop(false);
	server.addLibrary(StaticOne.class, 8270);
	server.start();
	String result = (String) runKeyword("stop_remote_server", new Object[0]).get("output");
	Assert.assertEquals(result, "This Robot Framework remote server does not allow stopping");
	server.setAllowStop(true);
	Thread.sleep(5000);
	result = (String) runKeyword("stop_remote_server", new Object[0]).get("output");
	Assert.assertEquals(result, "Robot Framework remote server stopping");
    }

    @Test
    public void serverIsRestartable() throws Exception {
	server = new RemoteServer();
	server.addLibrary(StaticOne.class, 8270);
	server.start();
	String result = (String) runKeyword("getName", new Object[0]).get("return");
	Assert.assertEquals(result, "StaticOne");
	server.stop();
	server.addLibrary(StaticTwo.class, 8270);
	server.start();
	result = (String) runKeyword("getName", new Object[0]).get("return");
	Assert.assertEquals(result, "StaticTwo");
	server.stop();
    }
    
    @Test
    public void cantLoadClass() {
	try {
	    server.addLibrary("TheAnswer", 56);
	} catch (RuntimeException e) {
	    Assert.assertEquals(e.getMessage(), "Unable to load class TheAnswer");
	}
    }

    @Test
    public void cantInstantiateClass() {
	Object anon = new Object() {
	};
	try {
	    server.addLibrary(anon.getClass(), 55);
	} catch (RuntimeException e) {
	    Assert.assertEquals(e.getMessage(), "Unable to create an instance of " + anon.getClass().getName());
	}
    }

    public XmlRpcClient getClient() {
	XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	try {
	    config.setServerURL(new URL("http://127.0.0.1:8270/"));
	} catch (MalformedURLException e) {
	    // ignore
	}
	XmlRpcClient client = new XmlRpcClient();
	client.setConfig(config);
	return client;
    }

    public Map runKeyword(String keywordName, Object[] params) {
	XmlRpcClient client = getClient();
	Map result = null;
	try {
	    result = (Map) client.execute("run_keyword", new Object[] { keywordName, params });
	} catch (XmlRpcException e) {
	    e.printStackTrace();
	}
	return result;
    }
    
    @AfterMethod
    public void setup() throws Exception {
	server = new RemoteServer();
    }

    @AfterMethod
    public void cleanup() {
	try {
	server.stop();
	} catch (Throwable t) {
	    // ignore
	}
    }
}

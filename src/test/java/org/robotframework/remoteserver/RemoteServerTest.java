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
import org.testng.annotations.Test;

public class RemoteServerTest {

    @Test
    public void serverIsRestartable() throws Exception {
	RemoteServer server = new RemoteServer();
	server.addLibrary(StaticOne.class, 8270);
	server.start();
	String result = (String) runKeyword("getName", new Object[0]);
	Assert.assertEquals(result, "StaticOne");
	server.stop();
	server.addLibrary(StaticTwo.class, 8270);
	server.start();
	result = (String) runKeyword("getName", new Object[0]);
	Assert.assertEquals(result, "StaticTwo");
	server.stop();
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
    
    public Object runKeyword(String keywordName, Object[] params) {
	XmlRpcClient client = getClient();
	Map result = null;
	try {
	    result = (Map) client.execute("run_keyword", new Object[] {keywordName, params});
	} catch (XmlRpcException e) {
	    e.printStackTrace();
	}
	return result.get("return");
    }
}

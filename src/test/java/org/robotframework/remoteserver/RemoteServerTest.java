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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RemoteServerTest {
    RemoteServer server;

    @Test
    public void allowRemoteStop() throws Exception {
        Assert.assertEquals(server.getAllowStop(), true);
        server.setAllowStop(false);
        server.putLibrary("/", StaticOne.class);
        server.start();
        String result = (String) runKeyword("/", "stop_remote_server").get("output");
        Assert.assertEquals(result, "This Robot Framework remote server does not allow stopping\n");
        server.setAllowStop(true);
        result = (String) runKeyword("/", "stop_remote_server").get("output");
        Assert.assertEquals(result, "Robot Framework remote server stopping\n");
    }

    @Test
    public void serverIsRestartable() throws Exception {
        server.putLibrary("/1", StaticOne.class);
        server.start();
        String result = (String) runKeyword("/1", "getName").get("return");
        Assert.assertEquals(result, "StaticOne");
        server.stop();
        server.putLibrary("/2", StaticTwo.class);
        server.start();
        result = (String) runKeyword("/2", "getName").get("return");
        Assert.assertEquals(result, "StaticTwo");
        server.stop();
    }

    public XmlRpcClient getClient(String path) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL("http://127.0.0.1:8270" + path));
        } catch (MalformedURLException e) {
            // ignore
        }
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        return client;
    }

    public Map runKeyword(String path, String keywordName, Object... params) {
        XmlRpcClient client = getClient(path);
        Map result = null;
        try {
            result = (Map) client.execute("run_keyword", new Object[] { keywordName, params });
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return result;
    }

    @BeforeMethod
    public void setup() throws Exception {
        server = new RemoteServer();
        server.setPort(8270);
    }

    @AfterMethod
    public void cleanup() throws InterruptedException {
        try {
            server.stop();
        } catch (Throwable t) {
            // ignore
        }
    }
}

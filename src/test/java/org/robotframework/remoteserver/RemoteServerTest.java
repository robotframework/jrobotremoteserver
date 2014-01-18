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
        Assert.assertEquals(result, "This Robot Framework remote server does not allow stopping");
        server.setAllowStop(true);
        result = (String) runKeyword("/", "stop_remote_server").get("output");
        Assert.assertEquals(result, "Robot Framework remote server stopping");
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

    @Test
    public void putLibrariesAfterStarting() throws Exception {
        server.start();
        server.putLibrary("/1", StaticOne.class);
        String result = (String) runKeyword("/1", "getName").get("return");
        Assert.assertEquals(result, "StaticOne");
    }

    @Test
    public void ephemeralPort() throws Exception {
        RemoteServer server = new RemoteServer();
        Assert.assertEquals(server.getLocalPort(), new Integer(-1));
        server.putLibrary("/", StaticOne.class);
        server.start();
        int port = server.getLocalPort();
        String result = (String) runKeyword(port, "/", "getName").get("return");
        Assert.assertEquals(result, "StaticOne");
        server.stop();
        Assert.assertEquals(server.getLocalPort(), new Integer(-2));
    }

    @Test
    public void addTwoLibrariesOnDifferentPorts() {
        server.addLibrary(StaticOne.class, 8270);
        Exception ex = null;
        try {
            server.addLibrary(StaticOne.class, 8271);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(ex.getMessage(),
                "Serving on multiple ports is no longer supported. Please use putLibrary with different paths instead.");
    }

    @Test
    public void mixAddLibraryWithSetPort() {
        server.setPort(8270);
        Exception ex = null;
        try {
            server.addLibrary(StaticOne.class, 8271);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(ex.getMessage(),
                "Serving on multiple ports is no longer supported. Please use putLibrary with different paths instead.");
    }

    @Test
    public void libraryMap() {
        server.putLibrary("/", StaticOne.class);
        Assert.assertTrue(server.getLibraryMap().containsKey("/"));
    }

    @Test
    public void onlyRequiredEntriesInResultsWhenPassed() throws Exception {
        server.putLibrary("/1", StaticOne.class);
        server.start();
        Map<?, ?> results = runKeyword("/1", "noReturnValue");
        Assert.assertEquals(results.get("status"), "PASS");
        Assert.assertEquals(results.size(), 1);
    }

    public XmlRpcClient getClient(String path) {
        return getClient(path, 8270);
    }

    public XmlRpcClient getClient(String path, int port) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL("http://127.0.0.1:" + Integer.toString(port) + path));
        } catch (MalformedURLException e) {
            // ignore
        }
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        return client;
    }

    public Map runKeyword(String path, String keywordName, Object... params) {
        return runKeyword(8270, path, keywordName, params);
    }

    public Map runKeyword(int port, String path, String keywordName, Object... params) {
        XmlRpcClient client = getClient(path, port);
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

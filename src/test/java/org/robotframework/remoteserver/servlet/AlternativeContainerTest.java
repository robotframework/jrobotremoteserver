package org.robotframework.remoteserver.servlet;

import static org.robotframework.remoteserver.RemoteLibraryClient.runKeyword;

import java.util.Properties;

import org.robotframework.remoteserver.testlibraries.StaticOne;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import Acme.Serve.Serve;

public class AlternativeContainerTest {

    private Serve server = null;

    @Test
    public void nonDefaultServletInTjws() throws Exception {
        server = new Serve();
        Properties properties = new Properties();
        properties.put("port", 9999);
        server.arguments = properties;
        RemoteServerServlet servlet = new RemoteServerServlet();
        servlet.putLibrary("/one", new StaticOne());
        server.addServlet("/foo/bar", servlet);
        server.addDefaultServlets(null);
        server.runInBackground();
        String name = (String) runKeyword(9999, "/foo/bar/one", "getName").get("return");
        Assert.assertEquals(name, "StaticOne");
    }

    @AfterMethod
    public void stopServer() throws Exception {
        if (server != null) {
            server.notifyStop();
        }
    }

}

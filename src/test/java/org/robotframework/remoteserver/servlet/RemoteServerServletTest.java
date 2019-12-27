package org.robotframework.remoteserver.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.robotframework.remoteserver.RemoteServer;
import org.robotframework.remoteserver.library.RemoteLibrary;
import org.robotframework.remoteserver.testlibraries.StaticOne;
import org.robotframework.remoteserver.testlibraries.StaticTwo;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RemoteServerServletTest {

    @Test
    public void putLibrary() {
        RemoteServerServlet servlet = new RemoteServerServlet();
        RemoteLibrary lib = servlet.putLibrary("/", new StaticOne());
        Assert.assertNull(lib);
        RemoteLibrary origLib = servlet.getLibraryMap().get("/");
        lib = servlet.putLibrary("/", new StaticTwo());
        Assert.assertEquals(lib, origLib);
    }

    @Test
    public void removeLibrary() {
        RemoteServerServlet servlet = new RemoteServerServlet();
        RemoteLibrary lib = servlet.removeLibrary("/");
        Assert.assertNull(lib);
        servlet.putLibrary("/", new StaticOne());
        RemoteLibrary origLib = servlet.getLibraryMap().get("/");
        lib = servlet.removeLibrary("/");
        Assert.assertEquals(lib, origLib);
    }

    private final String body = "<?xml version=\"1.0\"?><methodCall><methodName>get_keyword_names</methodName><params /></methodCall>";
}

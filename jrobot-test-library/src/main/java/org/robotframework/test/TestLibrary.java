package org.robotframework.test;

import org.robotframework.remoteserver.RemoteServer;

public class TestLibrary {

    public TestLibrary(RemoteServer server) {
        server.putLibrary("/" + getClass().getSimpleName(), this);
    }

}

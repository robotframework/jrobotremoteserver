package org.robotframework.test;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.remoteserver.RemoteServer;

@RobotKeywords public class ExtendedLibrary extends BaseLibrary {

    public ExtendedLibrary(RemoteServer server) {
        super(server);
    }

    @Override public String getURI() {
        return getClass().getSimpleName();
    }

    @Override public String getName() {
        return "Extended Library";
    }

    @RobotKeyword public double getPi() {
        return 3.14;
    }

}

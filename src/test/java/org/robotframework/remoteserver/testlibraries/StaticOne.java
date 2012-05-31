package org.robotframework.remoteserver.testlibraries;

public class StaticOne {
    public String getName() {
	return this.getClass().getSimpleName();
    }
}

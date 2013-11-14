package org.robotframework.remoteserver.servlet;

@SuppressWarnings("serial")
public class IllegalPathException extends RuntimeException {

    public IllegalPathException(String message) {
        super(message);
    }
}

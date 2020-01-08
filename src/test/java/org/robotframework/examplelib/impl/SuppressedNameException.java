package org.robotframework.examplelib.impl;

@SuppressWarnings("serial")
public class SuppressedNameException extends Exception {

    public static boolean ROBOT_SUPPRESS_NAME = true;

    public SuppressedNameException() {
        super("name suppressed");
    }
}

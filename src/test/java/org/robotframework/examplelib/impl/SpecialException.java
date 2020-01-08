package org.robotframework.examplelib.impl;

@SuppressWarnings("serial")
public class SpecialException extends RuntimeException {

    public boolean ROBOT_CONTINUE_ON_FAILURE = false;
    public boolean ROBOT_EXIT_ON_FAILURE = false;

    SpecialException(String message, boolean continuable, boolean fatal) {
        super(message);
        ROBOT_CONTINUE_ON_FAILURE = continuable;
        ROBOT_EXIT_ON_FAILURE = fatal;
    }

}

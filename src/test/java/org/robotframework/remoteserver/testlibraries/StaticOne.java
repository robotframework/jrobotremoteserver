package org.robotframework.remoteserver.testlibraries;

public class StaticOne {

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public int squareOf(int number) {
        return number * number;
    }

    public void variableArgs(String str, String...strings) {
    }

    public void onlyVariableArgs(String...strings) {
    }

}

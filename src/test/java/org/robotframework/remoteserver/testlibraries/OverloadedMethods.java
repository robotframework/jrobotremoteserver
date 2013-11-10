package org.robotframework.remoteserver.testlibraries;

public class OverloadedMethods {
    public void myKeyword() {
    }

    public void myKeyword(String name) {
    }

    public String numberType(short number) {
        return "short overload";
    }
    public String numberType(int number) {
        return "int overload";
    }

}

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

    public String defaults(String required) {
        return defaults("default");
    }

    public String defaults(String required, String optional) {
        return String.format("%s,%s", required, optional);
    }

}

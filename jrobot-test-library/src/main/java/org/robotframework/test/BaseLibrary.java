package org.robotframework.test;

import org.robotframework.remoteserver.RemoteServer;
import org.robotframework.remoteserver.library.AbstractClassLibrary;

public class BaseLibrary extends AbstractClassLibrary implements TestLibraryService {

    public BaseLibrary(RemoteServer server) {
        super(server);
    }

    @Override public String getURI() {
        return getClass().getSimpleName();
    }

    @Override public void close() {
    }

    @Override public String getName() {
        return "Base Library";
    }

    @Override public double add(double a, double b) {
        return a + b;
    }

    @Override public int add(int a, int b) {
        return a + b;
    }

    @Override public double sub(double a, double b) {
        return a - b;
    }

    @Override public int sub(int a, int b) {
        return a - b;
    }

    @Override public String concat(String input1, String input2) {
        return input1 + " " + input2;
    }

    @Override public String concat(String input1, int input2) {
        return input1 + " " + (input2 + 1);
    }

    @Override public String concat(int input1, String input2) {
        return (input1 + 1) + " " + input2;
    }

    @Override public String concat(String input1, String input2, String input3) {
        return input1 + " " + input2 + " " + input3;
    }
}

package org.robotframework.remoteserver.testlibraries;

public class DynamicOne extends StaticOne {

    public String[] getKeywordNames() {
        return new String[] {"getName"};
    }

    public Object runKeyword(String name, Object... args) {
        if (name.equals("genName")) {
            return getName();
        } else {
            throw new RuntimeException();
        }
    }

}

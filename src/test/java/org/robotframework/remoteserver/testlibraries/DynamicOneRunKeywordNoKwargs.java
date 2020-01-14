package org.robotframework.remoteserver.testlibraries;

import java.util.Arrays;
import java.util.List;

public class DynamicOneRunKeywordNoKwargs extends StaticOne {

    public List<String> getKeywordNames() {
        return Arrays.asList("getArgs");
    }

    public Object runKeyword(String name, List<String> args) {
        if (name.equals("getArgs")) {
            return getArgs(args);
        } else {
            throw new RuntimeException();
        }
    }

}

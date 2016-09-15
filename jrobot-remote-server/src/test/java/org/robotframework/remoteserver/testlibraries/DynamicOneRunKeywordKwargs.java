package org.robotframework.remoteserver.testlibraries;

import java.util.Map;

public class DynamicOneRunKeywordKwargs extends StaticOne {

    public String[] getKeywordNames() {
        return new String[] {"getArgs"};
    }

    public Object runKeyword(String name, Object[] args, Map kwargs) {
        if (name.equals("getArgs")) {
            return getArgs(args, kwargs);
        } else {
            throw new RuntimeException();
        }
    }

    public String[] getKeywordArguments(String name) {
        return new String[] {"*args", "**kwargs"}; 
    }

}

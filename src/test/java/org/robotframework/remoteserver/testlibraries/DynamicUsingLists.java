package org.robotframework.remoteserver.testlibraries;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class DynamicUsingLists {
    
    public List<String> getKeywordNames() {
        return Arrays.asList("go", "goList");
    }

    public Object runKeyword(String name, List<String> arguments) {
        Method[] methods = this.getClass().getMethods();
        try {
            for (Method meth : methods) {
                if (meth.getName().equals(name)) {
                    return meth.invoke(this, arguments.toArray());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("bad keyword");
    }

    public List<String> getKeywordArguments(String name) {
        if (name.equals("go")) {
            return Arrays.asList("where");
        } else if (name.equals("goList")) {
            return Arrays.asList("list");
        }
        throw new RuntimeException("bad keyword");
    }

    public String go(String where) {
        return where;
    }

    public List<String> goList(List<String> list) {
        return list;
    }

}

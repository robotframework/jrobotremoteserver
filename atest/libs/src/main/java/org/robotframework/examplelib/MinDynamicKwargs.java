package org.robotframework.examplelib;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MinDynamicKwargs {

    public String[] getKeywordNames() {
        return new String[] { "getKwargValue" };
    }

    public Object runKeyword(String name, List<?> args, Map<String, ?> kwargs) {
        if (name.equals("getKwargValue")) {
            assert(args.size() == 1);
            return getKwargValue((String) args.get(0), kwargs);
        } else {
            throw new RuntimeException();
        }
    }

    public List<String> getKeywordArguments(String name) {
        if (name.equals("getKwargValue")) {
            return Arrays.asList(new String[] {"key", "**kwargs"});
        }
        return null;
    }

    private Object getKwargValue(String key, Map<String, ?> kwargs) {
        return kwargs.get(key);
    }

}

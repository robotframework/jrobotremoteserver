package org.robotframework.examplelib;

import java.util.*;

public class MinDynamicKwargs {

    public List<String> getKeywordNames() {
        List<String> keywordNames = new ArrayList<String>();
        keywordNames.add("getKwargValue");
        return keywordNames;
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

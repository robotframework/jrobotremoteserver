package org.robotframework.remoteserver.testlibraries;

import java.util.Map;
import java.util.TreeMap;

public class StaticOne {

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public int squareOf(int number) {
        return number * number;
    }

    public void variableArgs(String str, String... strings) {
    }

    public void onlyVariableArgs(String... strings) {
    }

    protected String getArgs(Object[] args) {
        return getArgs(args, null, false);
    }

    protected String getArgs(Object[] args, Map<String, Object> kwargs) {
        return getArgs(args, kwargs, true);
    }

    private String getArgs(Object[] args, Map<String, Object> kwargs, boolean evalKwargs) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("[");
        for (Object arg : args) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append("'").append(arg).append("'");
        }
        sb.append("]");
        if (evalKwargs) {
            first = true;
            sb.append("{");
            Map<String, ?> map = new TreeMap<String, Object>(kwargs);
            for (String key : map.keySet()) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append("'").append(key).append("':'").append(map.get(key)).append("'");
            }
            sb.append("}");
        }
        return sb.toString();
    }

}

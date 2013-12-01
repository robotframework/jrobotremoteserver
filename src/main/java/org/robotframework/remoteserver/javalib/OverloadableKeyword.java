/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* This code is derived from JavalibCore 
 * Copyright 2008 Nokia Siemens Networks Oyj
 */
package org.robotframework.remoteserver.javalib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robotframework.javalib.keyword.Keyword;

public class OverloadableKeyword implements Keyword {

    private Map<Integer, List<KeywordOverload>> keywordMap = new HashMap<Integer, List<KeywordOverload>>();
    private String name;
    private Object keywordBean;
    private boolean usesVarArgs = false;

    public OverloadableKeyword(Object keywordBean, Method method) {
        name = method.getName();
        this.keywordBean = keywordBean;
        addOverload(method);
    }

    public Object execute(Object[] arguments) {
        KeywordOverload selectedKeyword = null;
        Integer argCount = arguments.length;
        if (usesVarArgs) {
            for (List<KeywordOverload> kws : keywordMap.values()) {
                selectedKeyword = kws.get(0);
            }
        } else if (keywordMap.containsKey(argCount)) {
            List<KeywordOverload> kwList = keywordMap.get(argCount);
            if (kwList.size() == 1) {
                selectedKeyword = kwList.get(0);
            } else {
                for (KeywordOverload overload : kwList) {
                    if (overload.canExecute(arguments)) {
                        selectedKeyword = overload;
                        break;
                    }
                }
                if (selectedKeyword == null) {
                    throw new IllegalArgumentException(String.format("No overload of %s can take the given arguments",
                            name));
                }
            }
        } else {
            if (keywordMap.size() == 1) {
                throw new IllegalArgumentException(String.format("%s takes %d argument(s), received %d.", name,
                        keywordMap.keySet().toArray()[0], argCount));
            } else {
                throw new IllegalArgumentException(String.format("No overload of %s takes %d argument(s).", name,
                        argCount));
            }
        }
        return selectedKeyword.execute(arguments);
    }

    public void addOverload(Method method) {
        Integer argCount = method.getParameterTypes().length;
        if (usesVarArgs || (!keywordMap.isEmpty() && hasVariableArgs(method))) {
            throw new RuntimeException(String.format(
                    "Method %s has overloads and one or more take variable arguments.", name));
        } else if (keywordMap.containsKey(argCount)) {
            keywordMap.get(argCount).add(new KeywordOverload(keywordBean, method));
        } else {
            List<KeywordOverload> overloadList = new ArrayList<KeywordOverload>();
            overloadList.add(new KeywordOverload(keywordBean, method));
            keywordMap.put(argCount, overloadList);
        }
        if (hasVariableArgs(method)) {
            usesVarArgs = true;
        }
    }

    public String[] getArguments() {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int argCount : keywordMap.keySet()) {
            if (argCount < min) {
                min = argCount;
            }
            if (argCount > max) {
                max = argCount;
            }
        }
        String[] arguments = new String[max];
        for (int i = 0; i < max; i++) {
            if (i < min) {
                arguments[i] = String.format("arg%d", i + 1);
            } else {
                arguments[i] = String.format("arg%d=", i + 1);
            }
        }
        if (usesVarArgs) {
            arguments[max - 1] = "*varargs";
        }
        return arguments;
    }

    private boolean hasVariableArgs(Method method) {
        Integer argCount = method.getParameterTypes().length;
        return (argCount > 0 && method.getParameterTypes()[argCount - 1].isArray());
    }

}

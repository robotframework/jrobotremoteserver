/* Copyright 2014 Kevin Ormbrek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robotframework.javalib.keyword.Keyword;

public class OverloadableKeyword implements Keyword {

    private Map<Integer, List<KeywordOverload>> keywordMap = new HashMap<Integer, List<KeywordOverload>>();
    private String name;
    private Object keywordBean;
    private boolean usesVarArgs = false;
    private static Log log = LogFactory.getLog(OverloadableKeyword.class);

    public OverloadableKeyword(Object keywordBean, Method method) {
        name = method.getName();
        this.keywordBean = keywordBean;
        addOverload(method);
    }

    public Object execute(List arguments) {
        return this.execute(arguments, null);
    }

    public Object execute(List arguments, Map kwargs) {
        KeywordOverload selectedKeyword = null;
        Integer argCount = arguments.size();
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
                    if (overload.canExecute(arguments, kwargs)) {
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
        return selectedKeyword.execute(arguments, kwargs);
    }

    public void addOverload(Method method) {
        Integer argCount = method.getParameterTypes().length;
        if (usesVarArgs || (!keywordMap.isEmpty() && hasVariableArgs(method))) {
            log.warn(String.format("Overloads with variable arguments not supported. Ignoring overload %s",
                    method.toString()));
            return;
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

    public List<String> getArguments() {
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
        List<String> arguments = new ArrayList<String>();
        for (int i = 0; i < max; i++) {
            if (i < min) {
                arguments.add(String.format("arg%d", i + 1));
            } else {
                arguments.add(String.format("arg%d=", i + 1));
            }
        }
        if (usesVarArgs) {
            arguments.set(max - 1, "*varargs");
        }
        return arguments;
    }

    private boolean hasVariableArgs(Method method) {
        Integer argCount = method.getParameterTypes().length;
        return (argCount > 0 && method.getParameterTypes()[argCount - 1].isArray());
    }

    @Override
    public List<String> getArgumentTypes() {
        return null;
    }
}

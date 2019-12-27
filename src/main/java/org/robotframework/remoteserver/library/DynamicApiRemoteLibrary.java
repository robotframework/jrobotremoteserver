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
package org.robotframework.remoteserver.library;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DynamicApiRemoteLibrary implements RemoteLibrary {
    private Object library;
    private Method getKeywordNames;
    private Method runKeyword;
    private Method getKeywordArguments;
    private Method getKeywordDocumentation;

    protected DynamicApiRemoteLibrary(Object library, Method getKeywordNames, Method runKeyword,
            Method getKeywordArguments, Method getKeywordDocumentation) {
        this.library = library;
        this.getKeywordNames = getKeywordNames;
        this.runKeyword = runKeyword;
        this.getKeywordArguments = getKeywordArguments;
        this.getKeywordDocumentation = getKeywordDocumentation;
    }

    @Override
    public List<String> getKeywordNames() {
        try {
            Object names = getKeywordNames.invoke(library, new Object[] {});
            if (names instanceof List) {
                return (List<String>) names;
            } else {
                return Arrays.asList((String[]) names);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Object runKeyword(String keyword, List<String> args, Map<String, Object> kwargs) throws Throwable {
        if (kwargs != null && !kwargs.isEmpty() && runKeyword.getParameterTypes().length == 2) {
            throw new RuntimeException("This library does not support keyword arguments.");
        }
        Object[] invokeArgs = new Object[runKeyword.getParameterTypes().length];
        invokeArgs[0] = keyword;
        invokeArgs[1] = runKeyword.getParameterTypes()[1].equals(List.class) ? args : args.toArray();
        if (invokeArgs.length == 3) {
            invokeArgs[2] = kwargs;
        }
        try {
            return runKeyword.invoke(library, invokeArgs);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Override
    public List<String> getKeywordArguments(String keyword) {
        if (getKeywordArguments == null)
            return Arrays.asList("*args");
        try {
            Object args = getKeywordArguments.invoke(library, keyword);
            if (args instanceof List) {
                return (List<String>) args;
            } else {
                return Arrays.asList((String[]) args);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String getKeywordDocumentation(String keyword) {
        if (getKeywordDocumentation == null)
            return "";
        try {
            return (String) getKeywordDocumentation.invoke(library, keyword);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return library.getClass().getName();
    }

    @Override
    public Object getImplementation() {
        return library;
    }
}

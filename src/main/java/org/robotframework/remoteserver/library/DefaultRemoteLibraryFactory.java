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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DefaultRemoteLibraryFactory implements RemoteLibraryFactory {

    public RemoteLibrary createRemoteLibrary(Object library) {
        if (library instanceof RemoteLibrary) {
            return (RemoteLibrary) library;
        }
        List<Method> methods = getPublicMethods(library.getClass());
        Method getKeywordNames = getGetKeywordNames(methods);
        Method runKeyword = getRunKeyword(methods);
        if (getKeywordNames == null || runKeyword == null) {
            return new StaticApiRemoteLibrary(library);
        }
        Method getKeywordArguments = getGetKeywordArguments(methods);
        Method getKeywordDocumentation = getGetKeywordDocumentation(methods);
        Method getKeywordTags = getGetKeywordTags(methods);
        Method getKeywordTypes = getGetKeywordTypes(methods);
        return new DynamicApiRemoteLibrary(library, getKeywordNames, runKeyword, getKeywordArguments,
                getKeywordDocumentation, getKeywordTags, getKeywordTypes);
    }

    private List<Method> getPublicMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<Method>();
        for (Method m : clazz.getMethods()) {
            if (Modifier.isPublic(m.getModifiers())) {
                methods.add(m);
            }
        }
        return methods;
    }

    private Method getGetKeywordNames(List<Method> methods) {
        for (Method m : methods) {
            if (((m.getName().equals("getKeywordNames") || (m.getName().equals("get_keyword_names")))
                    && (m.getReturnType() == String[].class || m.getReturnType() == List.class) && m
                        .getParameterTypes().length == 0)) {
                return m;
            }
        }
        return null;
    }

    private static List<Class<?>[]> runKeywordParamArrays = new ArrayList<Class<?>[]>();
    static {
        runKeywordParamArrays.add(new Class<?>[] { String.class, Object[].class, Map.class });
        runKeywordParamArrays.add(new Class<?>[] { String.class, List.class, Map.class });
        runKeywordParamArrays.add(new Class<?>[] { String.class, Object[].class });
        runKeywordParamArrays.add(new Class<?>[] { String.class, List.class });
    }

    private Method getRunKeyword(List<Method> methods) {
        Method matchingMethod = null;
        for (Method m : methods) {
            Class<?>[] pTypes = m.getParameterTypes();
            if ((m.getName().equals("runKeyword") || m.getName().equals("run_keyword"))
                    && m.getReturnType().equals(Object.class)) {
                for (Class<?>[] paramArray : runKeywordParamArrays) {
                    if (Arrays.equals(pTypes, paramArray)) {
                        if (pTypes.length == 3) {
                            return m;
                        } else {
                            matchingMethod = m;
                        }
                        break;
                    }
                }
            }
        }
        return matchingMethod;
    }

    private Method getGetKeywordArguments(List<Method> methods) {
        for (Method m : methods) {
            if ((m.getName().equals("getKeywordArguments") || m.getName().equals("get_keyword_arguments"))
                    && (m.getReturnType() == String[].class || m.getReturnType() == List.class)
                    && Arrays.equals(m.getParameterTypes(), new Class<?>[] { String.class }))
                return m;
        }
        return null;
    }

    private Method getGetKeywordDocumentation(List<Method> methods) {
        for (Method m : methods) {
            if ((m.getName().equals("getKeywordDocumentation") || m.getName().equals("get_keyword_documentation"))
                    && m.getReturnType() == String.class
                    && Arrays.equals(m.getParameterTypes(), new Class<?>[] { String.class }))
                return m;
        }
        return null;
    }

    private Method getGetKeywordTags(List<Method> methods) {
        for (Method m : methods) {
            if ((m.getName().equals("getKeywordTags") || m.getName().equals("get_keyword_tags"))
                    && m.getReturnType() == List.class
                    && Arrays.equals(m.getParameterTypes(), new Class<?>[] { String.class }))
                return m;
        }
        return null;
    }

    private Method getGetKeywordTypes(List<Method> methods) {
        for (Method m : methods) {
            if ((m.getName().equals("getKeywordTypes") || m.getName().equals("get_keyword_types"))
                    && m.getReturnType() == List.class
                    && Arrays.equals(m.getParameterTypes(), new Class<?>[] { String.class }))
                return m;
        }
        return null;
    }

}

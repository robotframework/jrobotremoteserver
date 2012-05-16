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
package org.robotframework.remoteserver;

import java.lang.reflect.Method;

public class RemoteLibraryFactory {
    public static IRemoteLibrary newRemoteLibrary(Object library) {
	Class<?> clazz = library.getClass();
	if (isDynamic(clazz))
	    return new DynamicApiLibrary(library, getMethod(clazz,
		    MethodType.GET_KEYWORD_NAMES), getMethod(clazz,
		    MethodType.RUN_KEYWORD), getMethod(clazz,
		    MethodType.GET_KEYWORD_ARGUMENTS), getMethod(clazz,
		    MethodType.GET_KEYWORD_DOCUMENTATION));
	else
	    return new StaticApiLibrary(library);
    }

    private static boolean isDynamic(Class<?> clazz) {
	return getMethod(clazz, MethodType.GET_KEYWORD_NAMES) != null
		&& getMethod(clazz, MethodType.RUN_KEYWORD) != null;
    }

    private static Method getMethod(Class<?> clazz, MethodType type) {
	for (Method m : clazz.getMethods()) {
	    String name = m.getName();
	    Class<?>[] params = m.getParameterTypes();
	    if (type.equals(MethodType.GET_KEYWORD_ARGUMENTS)
		    && (name.equals("getKeywordArguments") || name
			    .equals("get_keyword_arguments"))
		    && m.getReturnType() == String[].class
		    && params.length == 1 && params[0].equals(String.class))
		return m;
	    if (type.equals(MethodType.GET_KEYWORD_DOCUMENTATION)
		    && (name.equals("getKeywordDocumentation") || name
			    .equals("get_keyword_documentation"))
		    && m.getReturnType() == String.class && params.length == 1
		    && params[0].equals(String.class))
		return m;
	    if (type.equals(MethodType.GET_KEYWORD_NAMES)
		    && (name.equals("getKeywordNames") || name
			    .equals("get_keyword_names"))
		    && m.getReturnType() == String[].class
		    && params.length == 0)
		return m;
	    if (type.equals(MethodType.RUN_KEYWORD)
		    && (name.equals("runKeyword") || name.equals("run_keyword"))
		    && m.getReturnType().equals(Object.class)
		    && params.length == 2 && params[0].equals(String.class)
		    && params[1].equals(Object[].class))
		return m;
	}
	return null;
    }

    enum MethodType {
	GET_KEYWORD_ARGUMENTS, GET_KEYWORD_DOCUMENTATION, GET_KEYWORD_NAMES, RUN_KEYWORD;
    }
}

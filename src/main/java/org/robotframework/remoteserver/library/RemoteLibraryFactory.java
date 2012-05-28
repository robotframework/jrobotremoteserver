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
package org.robotframework.remoteserver.library;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class RemoteLibraryFactory {
    public static RemoteLibrary newRemoteLibrary(Object library) {
	Class<?> clazz = library.getClass();
	if (isDynamic(clazz)) {
	    Method getKeywordNames = getMethod(clazz, MethodType.GET_KEYWORD_NAMES);
	    Method runKeyword = getMethod(clazz, MethodType.RUN_KEYWORD);
	    Method getKeywordArguments = getMethod(clazz, MethodType.GET_KEYWORD_ARGUMENTS);
	    Method getKeywordDocumentation = getMethod(clazz, MethodType.GET_KEYWORD_DOCUMENTATION);
	    return new DynamicApiRemoteLibrary(library, getKeywordNames, runKeyword, getKeywordArguments,
		    getKeywordDocumentation);
	} else
	    return new StaticApiRemoteLibrary(library);
    }

    private static boolean isDynamic(Class<?> clazz) {
	return getMethod(clazz, MethodType.GET_KEYWORD_NAMES) != null
		&& getMethod(clazz, MethodType.RUN_KEYWORD) != null;
    }

    private static Method getMethod(Class<?> clazz, MethodType type) {
	for (Method m : clazz.getMethods()) {
	    if (!isEligibleMethod(m))
		continue;
	    String name = m.getName();
	    Class<?>[] pTypes = m.getParameterTypes();
	    if (type.equals(MethodType.GET_KEYWORD_ARGUMENTS)
		    && (name.equals("getKeywordArguments") || name.equals("get_keyword_arguments"))
		    && m.getReturnType() == String[].class && Arrays.equals(pTypes, new Class<?>[] { String.class }))
		return m;
	    if (type.equals(MethodType.GET_KEYWORD_DOCUMENTATION)
		    && (name.equals("getKeywordDocumentation") || name.equals("get_keyword_documentation"))
		    && m.getReturnType() == String.class && Arrays.equals(pTypes, new Class<?>[] { String.class }))
		return m;
	    if (type.equals(MethodType.GET_KEYWORD_NAMES)
		    && (name.equals("getKeywordNames") || name.equals("get_keyword_names"))
		    && m.getReturnType() == String[].class && pTypes.length == 0)
		return m;
	    if (type.equals(MethodType.RUN_KEYWORD) && (name.equals("runKeyword") || name.equals("run_keyword"))
		    && m.getReturnType().equals(Object.class)
		    && Arrays.equals(pTypes, new Class<?>[] { String.class, Object[].class }))
		return m;
	}
	return null;
    }

    private static boolean isEligibleMethod(Method method) {
	if (!Modifier.isPublic(method.getModifiers())) {
	    return false; // Ignore non-public methods
	}
	if (method.getDeclaringClass() == Object.class) {
	    return false; // Ignore methods from Object.class
	}
	return true;
    }

    enum MethodType {
	GET_KEYWORD_ARGUMENTS, GET_KEYWORD_DOCUMENTATION, GET_KEYWORD_NAMES, RUN_KEYWORD;
    }
}

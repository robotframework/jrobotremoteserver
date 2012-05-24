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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class StaticApiLibrary implements IRemoteLibrary {

    private Object library;

    protected StaticApiLibrary(Object library) {
	this.library = library;
    }

    public String[] getKeywordNames() {
	Class<?> cls = library.getClass();
	Method methlist[] = cls.getDeclaredMethods();
	String[] keywords = new String[methlist.length];
	for (int i = 0; i < methlist.length; i++) {
	    Method m = methlist[i];
	    keywords[i] = m.getName();
	}
	return keywords;
    }

    public Object runKeyword(String keyword, Object[] args) {
	Class<?> cls = library.getClass();
	Method methlist[] = cls.getDeclaredMethods();
	Class[] paramTypes = null;
	String retType = null;
	for (int i = 0; i < methlist.length; i++) {
	    Method m = methlist[i];
	    if (m.getName().equalsIgnoreCase(keyword)) {
		paramTypes = m.getParameterTypes();
		retType = m.getReturnType().toString();
	    }
	}
	try {
	    Method meth = cls.getMethod(keyword, paramTypes);
	    Object retObj = library.getClass().getMethod(keyword, paramTypes).invoke(cls.newInstance(), args);
	    return retObj;
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    /***
     * NOTE: Currently returns argument data type instead of name until we can fix it.
     */
    public String[] getKeywordArguments(String keyword) {
	// TODO - figure out how to best to implement this
	// with reflection. Given a "keyword" method name,
	// lookup method in Java class library that's
	// loaded into the server, and return a string array
	// of arguments (names) that method takes.

	// my knowledge of Java reflection is limited,
	// so for now, we do this...
	if (keyword.equalsIgnoreCase("stop_remote_server")) {
	    return new String[0];
	}
	try {
	    Class cls = library.getClass();
	    Method methlist[] = cls.getDeclaredMethods();
	    String[] args = new String[0];
	    for (int i = 0; i < methlist.length; i++) {
		Method m = methlist[i];
		if (m.getName().equalsIgnoreCase(keyword)) {
		    Class[] argsTmp = m.getParameterTypes();
		    args = new String[argsTmp.length];
		    for (int j = 0; j < args.length; j++) {
			args[j] = argsTmp[j].toString();
		    }
		}
	    }
	    return args;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /***
     * NOTE: Tentatively done by getting class annotation info plus return value of keyword method. Could enhance to
     * parse Javadoc info, etc.
     */
    public String getKeywordDocumentation(String keyword) {
	// TODO - figure out how to implement this.
	// Given a "keyword" method name, lookup method's
	// documentation, and return as string.
	// Use Javadoc API? Use doclet API?
	// Use Java annotations?
	// Look into how Robot Framework (non-remote)
	// Java/Jython libraries implement this
	// (with annotations, I believe)

	// my knowledge of Java reflection +
	// Java documentation is limited,
	// so for now, we do this...
	if (keyword.equalsIgnoreCase("stop_remote_server")) {
	    return "Stops the remote server.\n\nThe server may be configured so that users cannot stop it.";
	}
	try {
	    String doc = "";
	    Class cls = library.getClass();
	    Method methlist[] = cls.getDeclaredMethods();
	    for (int i = 0; i < methlist.length; i++) {
		Method m = methlist[i];
		if (m.getName().equalsIgnoreCase(keyword)) {
		    Annotation[] ann = m.getDeclaredAnnotations();
		    doc += "Annotations, if any:\n\n";
		    for (int j = 0; j < ann.length; j++) {
			doc += ann[j].toString() + "\n";
		    }
		    doc += "\nReturns: " + m.getReturnType().getSimpleName();
		}
	    }
	    return doc;
	} catch (Exception e) {
	    if (e.getClass().equals(java.lang.reflect.InvocationTargetException.class))
		throw new RuntimeException(e.getCause().getMessage(), e.getCause());
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public String getName() {
	return library.getClass().getName();
    }
}

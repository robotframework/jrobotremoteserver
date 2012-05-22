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

public class DynamicApiLibrary implements IRemoteLibrary {
    private Object library;
    private Method getKeywordNames;
    private Method runKeyword;
    private Method getKeywordArguments;
    private Method getKeywordDocumentation;

    protected DynamicApiLibrary(Object library, Method getKeywordNames, Method runKeyword, Method getKeywordArguments,
	    Method getKeywordDocumentation) {
	this.library = library;
	this.getKeywordNames = getKeywordNames;
	this.runKeyword = runKeyword;
	this.getKeywordArguments = getKeywordArguments;
	this.getKeywordDocumentation = getKeywordDocumentation;
    }

    public String[] getKeywordNames() {
	try {
	    return (String[]) getKeywordNames.invoke(library, new Object[] {});
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public Object runKeyword(String keyword, Object[] args) {
	try {
	    return runKeyword.invoke(library, keyword, args);
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public String[] getKeywordArguments(String keyword) {
	if (getKeywordArguments == null)
	    return new String[] { "*args" };
	try {
	    return (String[]) getKeywordArguments.invoke(library, keyword);
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public String getKeywordDocumentation(String keyword) {
	if (getKeywordDocumentation == null)
	    return "";
	try {
	    return (String) getKeywordDocumentation.invoke(library, keyword);
	} catch (Exception e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public String getName() {
	return library.getClass().getName();
    }
}

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

import org.robotframework.remoteserver.javalib.SingleClassLibrary;


public class StaticApiLibrary implements IRemoteLibrary {

    private Object library;
    private SingleClassLibrary handler;

    protected StaticApiLibrary(Object library) {
	handler = new SingleClassLibrary(library);
	this.library = library;
    }

    public String[] getKeywordNames() {
	return handler.getKeywordNames();
    }

    public Object runKeyword(String keywordName, Object[] args) {
	return handler.runKeyword(keywordName, args);
    }

    public String[] getKeywordArguments(String keyword) {
	return new String[] {"*args"};
    }

    public String getKeywordDocumentation(String keyword) {
	return "";
    }

    public String getName() {
	return library.getClass().getName();
    }
}

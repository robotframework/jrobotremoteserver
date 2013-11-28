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

import org.robotframework.remoteserver.javalib.SingleClassLibrary;

public class StaticApiRemoteLibrary implements RemoteLibrary {

    private Object library;
    private SingleClassLibrary handler;

    protected StaticApiRemoteLibrary(Object library) {
        handler = new SingleClassLibrary(library);
        this.library = library;
    }

    @Override
    public String[] getKeywordNames() {
        return handler.getKeywordNames();
    }

    @Override
    public Object runKeyword(String keywordName, Object[] args) {
        return handler.runKeyword(keywordName, args);
    }

    @Override
    public String[] getKeywordArguments(String keyword) {
        return new String[] { "*args" };
    }

    @Override
    public String getKeywordDocumentation(String keyword) {
        return "";
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

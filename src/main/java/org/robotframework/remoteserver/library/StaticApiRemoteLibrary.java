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

import java.util.List;
import java.util.Map;

import org.robotframework.remoteserver.javalib.SingleClassLibrary;

public class StaticApiRemoteLibrary implements RemoteLibrary {

    private Object library;
    private SingleClassLibrary handler;

    protected StaticApiRemoteLibrary(Object library) {
        handler = new SingleClassLibrary(library);
        this.library = library;
    }

    @Override
    public List<String> getKeywordNames() {
        return handler.getKeywordNames();
    }

    @Override
    public Object runKeyword(String keywordName, List<String> args, Map<String, Object> kwargs) throws Throwable {
        if (kwargs != null && !kwargs.isEmpty()) {
            throw new RuntimeException("Keyword arguments not yet supported for static API libraries.");
        }
        return handler.runKeyword(keywordName, args);
    }

    @Override
    public List<String> getKeywordArguments(String keyword) {
        return handler.getKeywordArguments(keyword);
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

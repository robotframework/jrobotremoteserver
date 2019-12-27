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

/**
 * An interface for handling libraries in jrobotremoteserver. There is no reason
 * for libraries to implement this.
 * <p>
 * User libraries are wrapped so that they can be handled in the same way. Use
 * {@link #getImplementation()} to access the wrapped library.
 *
 */
public interface RemoteLibrary {

    /**
     * Returns the names of keywords in the library.
     *
     * @return The names of keywords in the library.
     */
    List<String> getKeywordNames();

    /**
     * Executes the keyword with the given name. As some library implementations
     * may be case-, space-, or underscore-sensitive, it is best to use the name
     * as returned from {@link #getKeywordNames()}.
     *
     * @param name
     *            name of the keyword to execute
     * @param arguments
     *            positional arguments to the keyword
     * @param kwargs
     *            keyword arguments
     * @return value returned by the keyword
     * @throws Throwable exception thrown in case of error
     */
    public Object runKeyword(String name, List<String> arguments, Map<String, Object> kwargs) throws Throwable;

    /**
     * Gets the argument descriptors for the given keyword name.
     *
     * @param keyword
     *            name of the keyword to get argument specifications for
     * @return array of argument specifications
     */
    public List<String> getKeywordArguments(String keyword);

    /**
     * Gets the documentation string for the given keyword name.
     *
     * @param name
     *            name of the keyword to get documentation for
     * @return keyword documentation string
     */
    public String getKeywordDocumentation(String name);

    /**
     * Gets the list of tags for the given keyword name.
     *
     * @param name
     *            name of the keyword to get list of tags for
     * @return keyword list of tags
     */
    public List<String> getKeywordTags(String name);

    /**
     * Gets the list of argument types for the given keyword name.
     *
     * @param name
     *            name of the keyword to get argument types for
     * @return keyword argument types list
     */
    public List<String> getKeywordTypes(String name);

    /**
     * Gets the name of the remote library.
     *
     * @return The name of the remote library, which is the same as the class
     *         name
     */
    public String getName();

    /**
     * Gets the underlying library implementation. The {@link RemoteLibrary}
     * interface is intended to be used by wrapper classes. The original user
     * library can be accessed with this method.
     *
     * @return The underlying library implementation
     */
    public Object getImplementation();

}

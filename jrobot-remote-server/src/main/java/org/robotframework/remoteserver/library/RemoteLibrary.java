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

import java.util.Map;
import org.robotframework.javalib.library.KeywordDocumentationRepository;
import org.robotframework.javalib.library.RobotJavaLibrary;

/**
 * An interface for handling libraries in jrobotremoteserver. There is no reason
 * for libraries to implement this.
 * User libraries are wrapped so that they can be handled in the same way.
 */
public interface RemoteLibrary extends KeywordDocumentationRepository, RobotJavaLibrary, AutoCloseable {

    /**
     * Returns the names of keywords in the library.
     *
     * @return The names of keywords in the library.
     */
    String[] getKeywordNames();

    /**
     * Executes the keyword with the given name. As some library implementations
     * may be case-, space-, or underscore-sensitive, it is best to use the name
     * as returned from {@link #getKeywordNames()}.
     *
     * @param name      name of the keyword to execute
     * @param arguments positional arguments to the keyword
     * @param kwargs    keyword arguments
     * @return value returned by the keyword
     */
    Object runKeyword(String name, Object[] arguments, Map<String, Object> kwargs);

    /**
     * Gets the argument descriptors for the given keyword name.
     *
     * @param keyword name of the keyword to get argument specifications for
     * @return array of argument specifications
     */
    String[] getKeywordArguments(String keyword);

    /**
     * Gets the documentation string for the given keyword name.
     *
     * @param name name of the keyword to get documentation for
     * @return keyword documentation string
     */
    String getKeywordDocumentation(String name);

    /**
     * Gets the name of the remote library.
     *
     * @return The name of the remote library, which is the same as the class
     * name
     */
    String getURI();

    @Override void close();
}

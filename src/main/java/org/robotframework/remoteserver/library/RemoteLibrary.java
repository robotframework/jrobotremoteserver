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

/**
 * An interface for handling libraries in jrobotremoteserver. There is no reason
 * for libraries to implement this.
 * 
 */
public interface RemoteLibrary {

    /**
     * @return The keywords in the library
     */
    public String[] getKeywordNames();

    /**
     * @param keyword
     *            keyword to execute
     * @param args
     *            keyword arguments
     * @return value returned by the keyword
     */
    public Object runKeyword(String keyword, Object[] args);

    /**
     * @param keyword
     *            keyword to get argument descriptors for.
     * @return array of argument descriptors
     */
    public String[] getKeywordArguments(String keyword);

    /**
     * @param keyword
     *            keyword to get documentation for.
     * @return keyword documentation string
     */
    public String getKeywordDocumentation(String keyword);

    /**
     * @return The name of the remote library, which is the same as the class
     *         name.
     */
    public String getName();

    /**
     * @return The underlying library implementation
     */
    public Object getImplementation();

}

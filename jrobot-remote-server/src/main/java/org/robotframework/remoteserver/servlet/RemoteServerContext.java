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
package org.robotframework.remoteserver.servlet;

import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import org.robotframework.remoteserver.library.RemoteLibrary;

public interface RemoteServerContext extends Servlet {

    /**
     * Gets a copy of the current library map. Keys in the map are the paths and
     * the values are {@link RemoteLibrary} wrappers of the libraries being
     * served.
     *
     * @return a copy of the current library map
     */
    Map<String, RemoteLibrary> getLibraryMap();

    /**
     * Returns a {@link HttpServletRequest} object that contains the request the
     * client has made of the remote server servlet.
     *
     * @return {@link HttpServletRequest} object that contains the request the
     * client has made of the remote server servlet
     */
    HttpServletRequest getRequest();

    /**
     * Map the given test library to the specified path. Paths must:
     * <ul>
     * <li>start with a /</li>
     * <li>contain only alphanumeric characters or any of these: / - . _ ~</li>
     * <li>not end in a /</li>
     * <li>not contain a repeating sequence of /s</li>
     * </ul>
     * Example: <code>putLibrary("/myLib", new MyLibrary());</code>
     *
     * @param library instance of the test library
     * @param path    path to map the test library to
     * @return the previous library mapped to the path, or null if there was no
     * mapping for the path
     */
    RemoteLibrary putLibrary(String path, RemoteLibrary library);

    /**
     * Removes the library mapped to the given path if the mapping exists
     *
     * @param path path for the library whose mapping is to be removed
     * @return the previous library associated with the path, or null if there
     * was no mapping for the path.
     */
    RemoteLibrary removeLibrary(String path);

}

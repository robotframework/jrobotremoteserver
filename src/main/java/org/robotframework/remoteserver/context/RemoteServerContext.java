package org.robotframework.remoteserver.context;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.robotframework.remoteserver.library.RemoteLibrary;

public interface RemoteServerContext {

    /**
     * Returns <code>true</code> if this server allows remote stopping.
     * 
     * @return <code>true</code> if this server allows remote stopping
     */
    public boolean getAllowStop();

    /**
     * Gets a copy of the current library map. Keys in the map are the paths and
     * the values are {@link RemoteLibrary} wrappers of the libraries being
     * served.
     * 
     * @return a copy of the current library map
     */
    public Map<String, RemoteLibrary> getLibraryMap();

    /**
     * Returns a {@link HttpServletRequest} object that contains the request the
     * client has made of the remote server servlet.
     * 
     * @return {@link HttpServletRequest} object that contains the request the
     *         client has made of the remote server servlet
     */
    public HttpServletRequest getRequest();

    /**
     * Map the given test library to the specified path. Paths must:
     * <ul>
     * <li>start with a /</li>
     * <li>contain only alphanumeric characters or any of these: / - . _ ~</li>
     * <li>not end in a /</li>
     * <li>not contain a repeating sequence of /s</li>
     * </ul>
     * 
     * Example: <code>putLibrary("/myLib", com.example.MyLibrary);</code>
     * 
     * @param clazz
     *            class of the test library
     * @param path
     *            path to map the test library to
     * @return the previous library mapped to the path, or null if there was no
     *         mapping for the path
     */
    public RemoteLibrary putLibrary(String path, Class<?> clazz);

    /**
     * Removes the library mapped to the given path if the mapping exists
     * 
     * @param path
     *            path for the library whose mapping is to be removed
     * @return the previous library associated with the path, or null if there
     *         was no mapping for the path.
     */
    public RemoteLibrary removeLibrary(String path);

    /**
     * Allow or disallow stopping the server remotely.
     * 
     * @param allowed
     *            <code>true</code> to allow stopping the server remotely
     */
    public void setAllowStop(boolean allowed);

}

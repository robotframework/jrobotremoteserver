package org.robotframework.remoteserver.context;

import org.robotframework.remoteserver.RemoteServer;
import org.robotframework.remoteserver.library.RemoteLibrary;

public interface Context {
    /**
     * @return {@link RemoteServer} in the current context 
     */
    RemoteServer getRemoteServer();
    
    /**
     * @return {@link RemoteLibrary} in the current context 
     */
    RemoteLibrary getLibrary();
}

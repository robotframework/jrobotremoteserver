package org.robotframework.examplelib;

import org.robotframework.javalib.library.AnnotationLibrary;
import org.robotframework.remoteserver.library.RemoteLibrary;
import org.robotframework.remoteserver.library.RemoteLibraryFactory;
import org.robotframework.remoteserver.servlet.RemoteServerServlet;

public class SimpleRemoteServerServlet extends RemoteServerServlet {
	
	private static final long serialVersionUID = -2729050484180284488L;
	private final RemoteLibrary library;
	
	public SimpleRemoteServerServlet(AnnotationLibrary library) {
        RemoteLibraryFactory libraryFactory = createLibraryFactory();
        RemoteLibrary remoteLibrary = libraryFactory.createRemoteLibrary(library);
		this.library = remoteLibrary;
	}

    public RemoteLibrary getLibrary() {
    	return this.library;
    }
}

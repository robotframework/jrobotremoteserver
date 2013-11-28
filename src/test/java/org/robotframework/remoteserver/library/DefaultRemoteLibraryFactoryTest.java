package org.robotframework.remoteserver.library;

import org.robotframework.remoteserver.testlibraries.DynamicOne;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DefaultRemoteLibraryFactoryTest {

    @Test
    public void wrappedLibrariesAreNotWrapped() {
        DefaultRemoteLibraryFactory fact = new DefaultRemoteLibraryFactory();
        RemoteLibrary first = fact.createRemoteLibrary(new DynamicOne());
        RemoteLibrary second = fact.createRemoteLibrary(first);
        Assert.assertFalse(second.getImplementation() instanceof RemoteLibrary);
    }

}

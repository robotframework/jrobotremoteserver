package org.robotframework.remoteserver.library;

import org.robotframework.remoteserver.testlibraries.DynamicOne;
import org.robotframework.remoteserver.testlibraries.DynamicUsingLists;
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

    @Test
    public void dynamicLibrariesUsingLists() {
        DefaultRemoteLibraryFactory fact = new DefaultRemoteLibraryFactory();
        RemoteLibrary lib = fact.createRemoteLibrary(new DynamicUsingLists());
        Assert.assertTrue(lib instanceof DynamicApiRemoteLibrary);
    }

}

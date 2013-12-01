package org.robotframework.remoteserver.library;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.List;

import org.testng.Assert;

import org.robotframework.remoteserver.testlibraries.DynamicOne;
import org.robotframework.remoteserver.testlibraries.DynamicUsingLists;
import org.testng.annotations.Test;

public class DynamicApiRemoteLibraryTest {

    @Test
    public void getDynamicAPILibraryImplementation() throws Exception {
        DynamicOne lib = new DynamicOne();
        Method gkn = DynamicOne.class.getMethod("getKeywordNames");
        Method rk = DynamicOne.class.getMethod("runKeyword", new Class<?>[] { String.class, Object[].class });
        DynamicApiRemoteLibrary wrapper = new DynamicApiRemoteLibrary(lib, gkn, rk, null, null);
        assertEquals(wrapper.getImplementation(), lib);
    }

    @Test
    public void libraryUsingLists() throws Exception {
        DynamicUsingLists lib = new DynamicUsingLists();
        Method gkn = DynamicUsingLists.class.getMethod("getKeywordNames");
        Method rk = DynamicUsingLists.class.getMethod("runKeyword", new Class<?>[] { String.class, List.class });
        Method gka = DynamicUsingLists.class.getMethod("getKeywordArguments", new Class<?>[] { String.class });
        DynamicApiRemoteLibrary wrapper = new DynamicApiRemoteLibrary(lib, gkn, rk, gka, null);
        Assert.assertEquals(wrapper.getKeywordNames(), new String[] {"go"});
        Assert.assertEquals(wrapper.runKeyword("go", new Object[] {"there"}), "there");
        Assert.assertEquals(wrapper.getKeywordArguments("go"), new String[] {"where"});
    }

}

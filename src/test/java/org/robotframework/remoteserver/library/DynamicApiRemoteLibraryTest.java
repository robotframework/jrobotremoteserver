package org.robotframework.remoteserver.library;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;

import org.robotframework.remoteserver.testlibraries.DynamicOne;
import org.testng.annotations.Test;

public class DynamicApiRemoteLibraryTest {

    @Test
    public void getStaticAPILibraryImplementation() throws Exception {
        DynamicOne lib = new DynamicOne();
        Method gkn = DynamicOne.class.getMethod("getKeywordNames");
        Method rk = DynamicOne.class.getMethod("runKeyword", new Class<?>[] { String.class, Object[].class });
        DynamicApiRemoteLibrary wrapper = new DynamicApiRemoteLibrary(lib, gkn, rk, null, null);
        assertEquals(wrapper.getImplementation(), lib);
    }

}

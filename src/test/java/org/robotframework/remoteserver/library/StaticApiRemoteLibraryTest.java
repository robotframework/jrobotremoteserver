package org.robotframework.remoteserver.library;

import java.util.ArrayList;

import org.robotframework.remoteserver.testlibraries.StaticOne;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

public class StaticApiRemoteLibraryTest {

    @Test
    public void keywordNaming() throws Throwable {
        StaticApiRemoteLibrary lib = new StaticApiRemoteLibrary(new StaticOne());
        assertEquals(lib.runKeyword("get name", new ArrayList<String>(), null), "StaticOne");
        assertEquals(lib.runKeyword("_g e tn_a __ m_e_", new ArrayList<String>(), null), "StaticOne");
        assertEquals(lib.runKeyword(" G E T N A M E", new ArrayList<String>(), null), "StaticOne");
    }

    @Test
    public void getStaticAPILibraryImplementation() {
        StaticOne lib = new StaticOne();
        StaticApiRemoteLibrary wrapper = new StaticApiRemoteLibrary(lib);
        assertEquals(wrapper.getImplementation(), lib);
    }

}

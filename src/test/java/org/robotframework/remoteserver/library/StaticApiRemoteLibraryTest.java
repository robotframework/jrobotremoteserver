package org.robotframework.remoteserver.library;

import static org.testng.Assert.assertEquals;

import org.robotframework.remoteserver.testlibraries.StaticOne;
import org.testng.annotations.Test;

public class StaticApiRemoteLibraryTest {

    @Test
    public void keywordNaming() {
        StaticApiRemoteLibrary lib = new StaticApiRemoteLibrary(new StaticOne());
        assertEquals(lib.runKeyword("get name", new Object[] {}, null), "StaticOne");
        assertEquals(lib.runKeyword("_g e tn_a __ m_e_", new Object[] {}, null), "StaticOne");
        assertEquals(lib.runKeyword(" G E T N A M E", new Object[] {}, null), "StaticOne");
    }

    @Test
    public void getStaticAPILibraryImplementation() {
        StaticOne lib = new StaticOne();
        StaticApiRemoteLibrary wrapper = new StaticApiRemoteLibrary(lib);
        assertEquals(wrapper.getImplementation(), lib);
    }

}

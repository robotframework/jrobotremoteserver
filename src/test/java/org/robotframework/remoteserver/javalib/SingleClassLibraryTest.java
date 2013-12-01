package org.robotframework.remoteserver.javalib;

import org.robotframework.remoteserver.testlibraries.OverloadedMethods;
import org.robotframework.remoteserver.testlibraries.StaticOne;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SingleClassLibraryTest {

    @Test
    public void getKeywordArgumentsWithOverloadedMethods() {
        SingleClassLibrary lib = new SingleClassLibrary(new OverloadedMethods());
        String[] args = lib.getKeywordArguments("defaults");
        Assert.assertEquals(args, new String[] {"arg1", "arg2="});
        args = lib.getKeywordArguments("numberType");
        Assert.assertEquals(args, new String[] {"arg1"});
        args = lib.getKeywordArguments("myKeyword");
        Assert.assertEquals(args, new String[] {"arg1="});
    }

    @Test
    public void getKeywordArgumentsSingleMethod() {
        SingleClassLibrary lib = new SingleClassLibrary(new StaticOne());
        String[] args = lib.getKeywordArguments("getName");
        Assert.assertEquals(args, new String[] {});
        args = lib.getKeywordArguments("squareOf");
        Assert.assertEquals(args, new String[] {"arg1"});
        args = lib.getKeywordArguments("variableArgs");
        Assert.assertEquals(args, new String[] {"arg1", "*varargs"});
        args = lib.getKeywordArguments("onlyVariableArgs");
        Assert.assertEquals(args, new String[] {"*varargs"});
    }

}

package org.robotframework.remoteserver.javalib;

import org.robotframework.remoteserver.testlibraries.OverloadedMethods;
import org.robotframework.remoteserver.testlibraries.StaticOne;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class SingleClassLibraryTest {

    @Test
    public void getKeywordArgumentsWithOverloadedMethods() {
        SingleClassLibrary lib = new SingleClassLibrary(new OverloadedMethods());
        List<String> args = lib.getKeywordArguments("defaults");
        Assert.assertEquals(args, Arrays.asList("arg1", "arg2="));
        args = lib.getKeywordArguments("numberType");
        Assert.assertEquals(args, Arrays.asList("arg1"));
        args = lib.getKeywordArguments("myKeyword");
        Assert.assertEquals(args, Arrays.asList("arg1="));
    }

    @Test
    public void getKeywordArgumentsSingleMethod() {
        SingleClassLibrary lib = new SingleClassLibrary(new StaticOne());
        List<String> args = lib.getKeywordArguments("getName");
        Assert.assertEquals(args, Arrays.asList());
        args = lib.getKeywordArguments("squareOf");
        Assert.assertEquals(args, Arrays.asList("arg1"));
        args = lib.getKeywordArguments("variableArgs");
        Assert.assertEquals(args, Arrays.asList("arg1", "*varargs"));
        args = lib.getKeywordArguments("onlyVariableArgs");
        Assert.assertEquals(args, Arrays.asList("*varargs"));
    }

}

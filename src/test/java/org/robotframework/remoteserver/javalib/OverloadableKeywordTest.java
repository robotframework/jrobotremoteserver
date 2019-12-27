package org.robotframework.remoteserver.javalib;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.robotframework.remoteserver.testlibraries.OverloadedMethods;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class OverloadableKeywordTest
{
    private OverloadedMethods library = new OverloadedMethods();
    private Method shortOverload;
    private Method intOverload;
    private OverloadableKeyword keyword;

    @BeforeTest
    public void setup() throws Exception {
        shortOverload = library.getClass().getDeclaredMethod("numberType", new Class[] {short.class});
        intOverload = library.getClass().getDeclaredMethod("numberType", new Class[] {int.class});
        keyword = new OverloadableKeyword(library, shortOverload);
        keyword.addOverload(intOverload);
    }

    // this needs to be tested here because there are no guarantees about the
    // order of methods returned by getMethods()
    @Test
    public void picksFirstCompatibleOverload() throws Exception { 
        String result = (String) keyword.execute(Arrays.asList("32767"));
        Assert.assertEquals(result, "short overload");
        result = (String) keyword.execute(Arrays.asList("32768"));
        Assert.assertEquals(result, "int overload");
    }

    @Test
    public void noCompatibleOverload() throws Exception { 
        String message = null;
        Class<?> exceptionClass = null;
        try {
            keyword.execute(Arrays.asList("SNARF"));
        } catch (Exception e) {
            exceptionClass = e.getClass();
            message = e.getMessage();
        }
        Assert.assertEquals(exceptionClass, IllegalArgumentException.class);
        Assert.assertEquals(message, "No overload of numberType can take the given arguments");
    }
}

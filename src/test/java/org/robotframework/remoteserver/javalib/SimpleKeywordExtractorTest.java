package org.robotframework.remoteserver.javalib;

import org.robotframework.remoteserver.testlibraries.OverloadedMethods;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SimpleKeywordExtractorTest {
    @Test
    public void overloadedMethod() {
	SimpleKeywordExtractor keywordExtractor = new SimpleKeywordExtractor();
	String msg = null;
	try {
	    keywordExtractor.extractKeywords(new OverloadedMethods());
	} catch (RuntimeException e) {
	    msg = e.getMessage();
	}
	Assert.assertEquals(msg, "Overloaded method with name 'myKeyword' found!");
    }
}

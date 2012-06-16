package org.robotframework.remoteserver.javalib;

import org.robotframework.javalib.factory.KeywordFactory;
import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.remoteserver.testlibraries.DuplicateKeywords;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SimpleKeywordFactoryTest {

    @Test
    public void duplicateKeywords() {
	String msg = "";
	try {
	    KeywordFactory<Keyword> keywordFactory = new SimpleKeywordFactory(new DuplicateKeywords());
	} catch (RuntimeException e) {
	    msg = e.getMessage();
	}
	Assert.assertTrue(msg.matches("Two keywords with name 'my(_k|K)eyword' found!"));
    }
}
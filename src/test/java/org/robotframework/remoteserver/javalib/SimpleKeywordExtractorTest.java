package org.robotframework.remoteserver.javalib;

import java.util.Arrays;
import java.util.Map;

import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.remoteserver.testlibraries.ConflictingOverloadVariableArguments;
import org.robotframework.remoteserver.testlibraries.OverloadedMethods;
import org.robotframework.remoteserver.testlibraries.StaticOne;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SimpleKeywordExtractorTest {

    @Test
    public void overloadedMethodWrongNumberOfArgs() {
        SimpleKeywordExtractor keywordExtractor = new SimpleKeywordExtractor();
        Map<String, OverloadableKeyword> keywordMap = keywordExtractor.extractKeywords(new OverloadedMethods());
        try {
            keywordMap.get("myKeyword").execute(Arrays.asList("name", 42));
        } catch (RuntimeException e) {
            Assert.assertEquals(e.getMessage(), "No overload of myKeyword takes 2 argument(s).");
            return;
        }
        throw new RuntimeException("No exception thrown");
    }

    @Test
    public void nonOverloadedMethodWrongNumberOfArgs() {
        SimpleKeywordExtractor keywordExtractor = new SimpleKeywordExtractor();
        Map<String, OverloadableKeyword> keywordMap = keywordExtractor.extractKeywords(new StaticOne());
        try {
            keywordMap.get("getName").execute(Arrays.asList("badArg"));
        } catch (RuntimeException e) {
            Assert.assertEquals(e.getMessage(), "getName takes 0 argument(s), received 1.");
            return;
        }
        throw new RuntimeException("No exception thrown");
    }

}

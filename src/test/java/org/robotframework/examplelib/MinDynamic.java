package org.robotframework.examplelib;

import java.util.List;

public class MinDynamic{

    private final FullDynamic lib = new FullDynamic();
    
    public List<String> getKeywordNames() {
        return lib.getKeywordNames();
    }
    
    public Object runKeyword(String keywordName, List<String> args) throws Throwable {
        return lib.runKeyword(keywordName, args);
    }
}
package org.robotframework.examplelib;

public class MinDynamic{

    private final FullDynamic lib = new FullDynamic();
    
    public String[] getKeywordNames() {
        return lib.getKeywordNames();
    }
    
    public Object runKeyword(String keywordName, Object[] args) throws Throwable {
        return lib.runKeyword(keywordName, args);
    }
}
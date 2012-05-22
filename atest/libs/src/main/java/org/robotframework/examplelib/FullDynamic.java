package org.robotframework.examplelib;

import org.robotframework.examplelib.impl.Keywords;
import org.robotframework.javalib.library.AnnotationLibrary;
import org.robotframework.javalib.util.KeywordNameNormalizer;

public class FullDynamic {
    public FullDynamic() {
        lib = new AnnotationLibrary();
        lib.addKeywordPattern("org/robotframework/examplelib/impl/**.class");
    }

    public String[] getKeywordNames() {
        return lib.getKeywordNames();
    }
    
    /* JavaLibCore's invoker re-throws all exceptions as RuntimeExceptions.  This
     * allows us to bypass it (cannot override and declare throws Exception).
     */
    public Object runKeyword(String keywordName, Object[] args) throws Exception {
        String nname = normalizer.normalize(keywordName);
        if (nname.equals("baseexception"))
            Keywords.baseException();
        else if (nname.equals("exceptionwithoutmessage"))
            Keywords.exceptionWithoutMessage();
        else if (nname.equals("assertionerror"))
            Keywords.assertionError();
        else if (nname.equals("indexerror"))
            Keywords.indexError();
        else if (nname.equals("zerodivision"))
            Keywords.zeroDivision();
        else if (nname.equals("customexception"))
            Keywords.customException();
        else if (nname.equals("failing"))
            Keywords.failing(args[0].toString());
        else if (nname.equals("failuredeeper"))
            Keywords.failureDeeper(args);
        else
            return lib.runKeyword(keywordName, args);
        throw new Exception("should be unreachable!");
    }
    
    public String[] getKeywordArguments(String keywordName) {
        return lib.getKeywordArguments(keywordName);
    }
    
    public String getKeywordDocumentation(String keywordName) {
        if (keywordName.equals("__intro__"))
            return "This is an example dyanmic API library.";
        else
            return lib.getKeywordDocumentation(keywordName);
    }
    
    private AnnotationLibrary lib;
    
    private KeywordNameNormalizer normalizer = new KeywordNameNormalizer();
}

package org.robotframework.examplelib;

import org.robotframework.javalib.library.AnnotationLibrary;

public class FullDynamic {
    public FullDynamic() {
        lib = new AnnotationLibrary();
        lib.addKeywordPattern("org/robotframework/examplelib/impl/**.class");
    }

    public String[] getKeywordNames() {
        return lib.getKeywordNames();
    }

    /*
     * AnnotationLibrary re-throws all exceptions as RuntimeExceptions. Unwrap
     * it to obtain the original exception.
     */
    public Object runKeyword(String keywordName, Object[] args) throws Exception {
        try {
            return lib.runKeyword(keywordName, args);
        } catch (RuntimeException e) {
            throw (Exception) e.getCause();
        }
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

}

package org.robotframework.examplelib;

import org.robotframework.javalib.library.AnnotationLibrary;

import java.util.List;

public class FullDynamic {
    public FullDynamic() {
        lib = new AnnotationLibrary();
        lib.addKeywordPattern("org/robotframework/examplelib/impl/**.class");
    }

    public List<String> getKeywordNames() {
        return lib.getKeywordNames();
    }

    /*
     * AnnotationLibrary re-throws all exceptions as RuntimeExceptions. Unwrap
     * it to obtain the original exception.
     */
    public Object runKeyword(String keywordName, List<String> args) throws Throwable {
        try {
            return lib.runKeyword(keywordName, args, null);
        } catch (RuntimeException e) {
            throw e.getCause();
        }
    }

    public List<String> getKeywordArguments(String keywordName) {
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

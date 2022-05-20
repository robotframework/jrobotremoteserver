package org.robotframework.examplelib;

import org.robotframework.javalib.library.AnnotationLibrary;
import org.robotframework.javalib.library.RobotFrameworkDynamicAPI;

import java.util.List;
import java.util.Map;

public class FullDynamic implements RobotFrameworkDynamicAPI
{
    private final AnnotationLibrary lib;
    
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
    @Override
    public Object runKeyword(String keywordName, List args) {
        return lib.runKeyword(keywordName, args, null);
    }

	@Override
	public Object runKeyword(String keywordName, List args, Map kwargs) {
        return lib.runKeyword(keywordName, args, kwargs);
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


	public AnnotationLibrary getLib() {
		return lib;
	}

}

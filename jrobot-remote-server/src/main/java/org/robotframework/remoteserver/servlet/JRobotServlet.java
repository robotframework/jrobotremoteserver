package org.robotframework.remoteserver.servlet;

import java.util.Map;

/**
 * Contains the XML-RPC methods for remote library interface.
 */
public interface JRobotServlet {

    /**
     * Get an array containing the names of the keywords that the library
     * implements.
     *
     * @return String array containing keyword names in the library
     */
    String[] get_keyword_names();

    /**
     * Run the given keyword and return the results.
     *
     * @param keyword keyword to run
     * @param args    arguments packed in an array to pass to the keyword method
     * @param kwargs  keyword arguments to pass to the keyword method
     * @return remote result Map containing the execution results
     */
    Map<String, Object> run_keyword(String keyword, Object[] args, Map<String, Object> kwargs);

    /**
     * Run the given keyword and return the results.
     *
     * @param keyword keyword to run
     * @param args    arguments packed in an array to pass to the keyword method
     * @return remote result Map containing the execution results
     */
    Map<String, Object> run_keyword(String keyword, Object[] args);

    /**
     * Get an array of argument specifications for the given keyword.
     *
     * @param keyword The keyword to lookup.
     * @return A string array of argument specifications for the given keyword.
     */
    String[] get_keyword_arguments(String keyword);

    /**
     * Get documentation for given keyword.
     *
     * @param keyword The keyword to get documentation for.
     * @return A documentation string for the given keyword.
     */
    String get_keyword_documentation(String keyword);
}

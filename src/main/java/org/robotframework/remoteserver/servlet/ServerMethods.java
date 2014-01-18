/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robotframework.remoteserver.servlet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robotframework.javalib.util.StdStreamRedirecter;
import org.robotframework.remoteserver.context.Context;

/**
 * Contains the XML-RPC methods that implement the remote library interface.
 * 
 * @author David Luu
 * 
 */
public class ServerMethods {

    private Log log;
    private Context context;
    private static List<String> genericExceptions = Arrays.asList(new String[] { "AssertionError",
            "AssertionFailedError", "Exception", "Error", "RuntimeError", "RuntimeException", "DataError",
            "TimeoutError", "RemoteError" });

    public ServerMethods(Context context) {
        log = LogFactory.getLog(ServerMethods.class);
        this.context = context;
    }

    /**
     * Get an array containing the names of the keywords that the library
     * implements.
     * 
     * @return String array containing keyword names in the library
     */
    public String[] get_keyword_names() {
        try {
            String[] names = context.getLibrary().getKeywordNames();
            if (names == null || names.length == 0)
                throw new RuntimeException("No keywords found in the test library");
            String[] newNames = Arrays.copyOf(names, names.length + 1);
            newNames[names.length] = "stop_remote_server";
            return newNames;
        } catch (Throwable e) {
            log.warn("", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Run the given keyword and return the results.
     * 
     * @param keyword
     *            keyword to run
     * @param args
     *            arguments packed in an array to pass to the keyword method
     * @param kwargs
     *            keyword arguments to pass to the keyword method
     * @return remote result Map containing the execution results
     */
    public Map<String, Object> run_keyword(String keyword, Object[] args, Map<String, Object> kwargs) {
        Map<String, Object> result = new HashMap<String, Object>();
        StdStreamRedirecter redirector = new StdStreamRedirecter();
        redirector.redirectStdStreams();
        try {
            result.put("status", "PASS");
            Object retObj = "";
            if (keyword.equalsIgnoreCase("stop_remote_server")) {
                retObj = stopRemoteServer();
            } else {
                try {
                    retObj = context.getLibrary().runKeyword(keyword, args, kwargs);
                } catch (Exception e) {
                    if (illegalArgumentIn(e)) {
                        for (int i = 0; i < args.length; i++)
                            args[i] = arraysToLists(args[i]);
                        retObj = context.getLibrary().runKeyword(keyword, args, kwargs);
                    } else {
                        throw (e);
                    }
                }
            }
            if (retObj != null && !retObj.equals("")) {
                result.put("return", retObj);
            }
        } catch (Throwable e) {
            result.put("status", "FAIL");
            Throwable t = e.getCause() == null ? e : e.getCause();
            result.put("error", getError(t));
            result.put("traceback", ExceptionUtils.getStackTrace(t));
        } finally {
            String stdOut = StringUtils.defaultString(redirector.getStdOutAsString());
            String stdErr = StringUtils.defaultString(redirector.getStdErrAsString());
            if (!stdOut.isEmpty() || !stdErr.isEmpty()) {
                String delimiter = stdOut.isEmpty() || stdErr.isEmpty() ? "" : "\n";
                result.put("output", stdOut + delimiter + stdErr);
            }
            redirector.resetStdStreams();
        }
        return result;
    }

    /**
     * Run the given keyword and return the results.
     * 
     * @param keyword
     *            keyword to run
     * @param args
     *            arguments packed in an array to pass to the keyword method
     * @return remote result Map containing the execution results
     */
    public Map<String, Object> run_keyword(String keyword, Object[] args) {
        return run_keyword(keyword, args, null);
    }

    /**
     * Get an array of argument specifications for the given keyword.
     * 
     * @param keyword
     *            The keyword to lookup.
     * @return A string array of argument specifications for the given keyword.
     */
    public String[] get_keyword_arguments(String keyword) {
        if (keyword.equalsIgnoreCase("stop_remote_server")) {
            return new String[0];
        }
        try {
            String[] args = context.getLibrary().getKeywordArguments(keyword);
            return args == null ? new String[0] : args;
        } catch (Throwable e) {
            log.warn("", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get documentation for given keyword.
     * 
     * @param keyword
     *            The keyword to get documentation for.
     * @return A documentation string for the given keyword.
     */
    public String get_keyword_documentation(String keyword) {
        if (keyword.equalsIgnoreCase("stop_remote_server")) {
            return "Stops the remote server.\n\nThe server may be configured so that users cannot stop it.";
        }
        try {
            String doc = context.getLibrary().getKeywordDocumentation(keyword);
            return doc == null ? "" : doc;
        } catch (Throwable e) {
            log.warn("", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Stops the remote server if it is configured to allow that.
     * 
     * @return remote result Map containing the execution results
     */
    public Map<String, Object> stop_remote_server() {
        return run_keyword("stop_remote_server", null);
    }

    protected boolean stopRemoteServer() throws Exception {
        if (context.getRemoteServer().getAllowStop()) {
            System.out.print("Robot Framework remote server stopping");
            context.getRemoteServer().stop(2000);
        } else {
            System.out.print("This Robot Framework remote server does not allow stopping");
        }
        return true;
    }

    private String getError(Throwable thrown) {
        String simpleName = thrown.getClass().getSimpleName();
        boolean suppressName = false;
        try {
            suppressName = thrown.getClass().getField("ROBOT_SUPPRESS_NAME").getBoolean(thrown);
        } catch (Exception e) {
            // ignore
        }
        if (genericExceptions.contains(simpleName) || suppressName) {
            return StringUtils.defaultIfEmpty(thrown.getMessage(), simpleName);
        } else {
            return String.format("%s: %s", thrown.getClass().getName(), thrown.getMessage());
        }
    }

    protected Object arraysToLists(Object arg) {
        if (arg instanceof Object[]) {
            Object[] array = (Object[]) arg;
            List<Object> list = Arrays.asList(array);
            for (int i = 0; i < list.size(); i++)
                list.set(i, arraysToLists(list.get(i)));
            return list;
        } else if (arg instanceof Map<?, ?>) {
            Map<?, ?> oldMap = (Map<?, ?>) arg;
            Map<Object, Object> newMap = new HashMap<Object, Object>();
            for (Object key : oldMap.keySet())
                newMap.put(key, arraysToLists(oldMap.get(key)));
            return newMap;
        } else
            return arg;
    }

    private boolean illegalArgumentIn(Throwable t) {
        Throwable inner = t;
        while (inner.getCause() != null) {
            inner = inner.getCause();
            if (inner.getClass().equals(IllegalArgumentException.class)) {
                return true;
            }
        }
        return false;
    }
}

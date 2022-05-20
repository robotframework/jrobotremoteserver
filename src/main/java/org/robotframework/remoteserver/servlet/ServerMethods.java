/* Copyright 2014 Kevin Ormbrek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.robotframework.javalib.util.StdStreamRedirecter;

/**
 * Contains the XML-RPC methods that implement the remote library interface.
 *
 * @author David Luu
 *
 */
public class ServerMethods {

    private Log log;
    private RemoteServerServlet servlet;
    private static List<String> genericExceptions = Arrays.asList(new String[] { "AssertionError",
            "AssertionFailedError", "Exception", "Error", "RuntimeError", "RuntimeException", "DataError",
            "TimeoutError", "RemoteError" });
    String[] logLevelPrefixes = new String[] { "*TRACE*", "*DEBUG*", "*INFO*", "*HTML*", "*WARN*" };

    public ServerMethods(RemoteServerServlet servlet) {
        log = LogFactory.getLog(ServerMethods.class);
        this.servlet = servlet;
    }
    
    private static final String STOP_REMOTE_SERVER = "stop_remote_server";

    /**
     * Get an array containing the names of the keywords that the library
     * implements.
     *
     * @return String array containing keyword names in the library
     */
    public List<String> get_keyword_names() {
        try {
            List<String> names = servlet.getLibrary().getKeywordNames();
            if (names == null || names.size() == 0)
                throw new RuntimeException("No keywords found in the test library");
            if (!names.contains(STOP_REMOTE_SERVER)) {
            	names.add(STOP_REMOTE_SERVER);
            }
            return names;
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
    public Map<String, Object> run_keyword(String keyword, List<String> args, Map<String, Object> kwargs) {
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
                    retObj = servlet.getLibrary().runKeyword(keyword, args, kwargs);
                } catch (Exception e) {
                    if (illegalArgumentIn(e)) {
                        retObj = servlet.getLibrary().runKeyword(keyword, args, kwargs);
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
            Throwable thrown = e.getCause() == null ? e : e.getCause();
            result.put("error", getError(thrown));
            result.put("traceback", ExceptionUtils.getStackTrace(thrown));
            boolean continuable = isFlagSet("ROBOT_CONTINUE_ON_FAILURE", thrown);
            if (continuable) {
                result.put("continuable", true);
            }
            boolean fatal = isFlagSet("ROBOT_EXIT_ON_FAILURE", thrown);
            if (fatal) {
                result.put("fatal", true);
            }
        } finally {
            String stdOut = StringUtils.defaultString(redirector.getStdOutAsString());
            String stdErr = StringUtils.defaultString(redirector.getStdErrAsString());
            if (!stdOut.isEmpty() || !stdErr.isEmpty()) {
                StringBuilder output = new StringBuilder(stdOut);
                if (!stdOut.isEmpty() && !stdErr.isEmpty()) {
                    if (!stdOut.endsWith("\n")) {
                        output.append("\n");
                    }
                    boolean addLevel = true;
                    for (String prefix : logLevelPrefixes) {
                        if (stdErr.startsWith(prefix)) {
                            addLevel = false;
                            break;
                        }
                    }
                    if (addLevel) {
                        output.append("*INFO*");
                    }
                }
                result.put("output", output.append(stdErr).toString());
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
    public Map<String, Object> run_keyword(String keyword, List<String> args) {
        return run_keyword(keyword, args, null);
    }

    /**
     * Get an array of argument specifications for the given keyword.
     *
     * @param keyword
     *            The keyword to lookup.
     * @return A string array of argument specifications for the given keyword.
     */
    public List<String> get_keyword_arguments(String keyword) {
        if (keyword.equalsIgnoreCase("stop_remote_server")) {
            return Arrays.asList();
        }
        try {
            List<String> args = servlet.getLibrary().getKeywordArguments(keyword);
            return args == null ? Arrays.<String>asList() : args;
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
            String doc = servlet.getLibrary().getKeywordDocumentation(keyword);
            return doc == null ? "" : doc;
        } catch (Throwable e) {
            log.warn("", e);
            throw new RuntimeException(e);
        }
    }

    public List<String> get_keyword_tags(String keyword) {
        List<String> tags = servlet.getLibrary().getKeywordTags(keyword);
        return tags == null ? new ArrayList<String>() : tags;
    }

    public List<String> get_keyword_types(String keyword) {
        List<String> types = servlet.getLibrary().getKeywordTypes(keyword);
        return types == null ? new ArrayList<String>() : types;
    }

    public Map<String, Object> get_library_information() {
        return get_keyword_names().stream()
			.map(k->new AbstractMap.SimpleEntry<>(k, getLibraryInformation(k)))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Object getLibraryInformation(String keyword) {
        Map<String, Object> info = new HashMap<>();
        info.put("args", get_keyword_arguments(keyword));
        info.put("types", get_keyword_types(keyword));
        info.put("tags", get_keyword_tags(keyword));
        info.put("doc", get_keyword_documentation(keyword));
        return info;
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
        if (servlet.getAllowStop()) {
            System.out.print("Robot Framework remote server stopping");
            new Thread("remote-server-stopper") {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    System.exit(0);
                }
            }.start();
        } else {
            System.out.print("This Robot Framework remote server does not allow stopping");
        }
        return true;
    }

    private String getError(Throwable thrown) {
        String simpleName = thrown.getClass().getSimpleName();
        boolean suppressName = isFlagSet("ROBOT_SUPPRESS_NAME", thrown);
        if (genericExceptions.contains(simpleName) || suppressName) {
            return StringUtils.defaultIfEmpty(thrown.getMessage(), simpleName);
        } else {
            return String.format("%s: %s", thrown.getClass().getName(), thrown.getMessage());
        }
    }

    private boolean isFlagSet(String name, Throwable thrown) {
        boolean flag = false;
        try {
            flag = thrown.getClass().getField(name).getBoolean(thrown);
        } catch (Exception e) {
            // ignore
        }
        return flag;
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
        if (t.getClass().equals(IllegalArgumentException.class)) {
            return true;
        }
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

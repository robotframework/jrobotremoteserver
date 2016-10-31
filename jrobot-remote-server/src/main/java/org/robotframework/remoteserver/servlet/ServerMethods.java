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

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.robotframework.javalib.util.StdStreamRedirecter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the XML-RPC methods that implement the remote library interface.
 *
 * @author David Luu
 */
public class ServerMethods implements JRobotServlet {

    protected static final Logger LOG = LoggerFactory.getLogger(ServerMethods.class.getName());
    private static final List<String>
            genericExceptions =
            Arrays.asList("AssertionError", "AssertionFailedError", "Exception", "Error", "RuntimeError",
                    "RuntimeException", "DataError", "TimeoutError", "RemoteError");
    private static final String[] logLevelPrefixes = new String[] {"*TRACE*", "*DEBUG*", "*INFO*", "*HTML*", "*WARN*"};
    private final RemoteServerServlet servlet;

    public ServerMethods(RemoteServerServlet servlet) {
        this.servlet = servlet;
    }

    @Override public String[] get_keyword_names() {
        final String[] names = servlet.getLibrary().getKeywordNames();
        if (names == null || names.length == 0)
            throw new RuntimeException("No keywords found in the test library");
        return names;
    }

    @Override public Map<String, Object> run_keyword(String keyword, Object[] args, Map<String, Object> kwargs) {
        Map<String, Object> result = new HashMap<>();
        StdStreamRedirecter redirector = new StdStreamRedirecter();
        redirector.redirectStdStreams();
        try {
            result.put("status", "PASS");
            Object retObj;
            try {
                retObj = servlet.getLibrary().runKeyword(keyword, args, kwargs);
            } catch (Exception e) {
                if (illegalArgumentIn(e)) {
                    for (int i = 0; i < args.length; i++)
                        args[i] = arraysToLists(args[i]);
                    retObj = servlet.getLibrary().runKeyword(keyword, args, kwargs);
                } else {
                    throw (e);
                }
            }
            if (retObj != null && !retObj.equals("")) {
                result.put("return", retObj);
            }
        } catch (Throwable e) {
            result.put("status", "FAIL");
            Throwable thrown = e.getCause() == null ? e : e.getCause();
            result.put("error", getError(thrown));
            result.put("traceback", Throwables.getStackTraceAsString(thrown));
            boolean continuable = isFlagSet("ROBOT_CONTINUE_ON_FAILURE", thrown);
            if (continuable) {
                result.put("continuable", true);
            }
            boolean fatal = isFlagSet("ROBOT_EXIT_ON_FAILURE", thrown);
            if (fatal) {
                result.put("fatal", true);
            }
        } finally {
            String stdOut = Strings.nullToEmpty(redirector.getStdOutAsString());
            String stdErr = Strings.nullToEmpty(redirector.getStdErrAsString());
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

    @Override public Map<String, Object> run_keyword(String keyword, Object[] args) {
        Map<String, Object> kwargs = new HashMap<>();
        for (Object arg : Objects.requireNonNull(args)) {
            if (arg.toString().contains("=")) {
                kwargs.put(arg.toString().split("=")[0] + "=", arg.toString().split("=")[1]);
            }
        }
        return run_keyword(keyword, args, kwargs.isEmpty() ? Collections.emptyMap() : kwargs);
    }

    @Override public String[] get_keyword_arguments(String keyword) {
        final String[] args = servlet.getLibrary().getKeywordArguments(keyword);
        return args == null ? new String[0] : args;
    }

    @Override public String get_keyword_documentation(String keyword) {
        final String doc = servlet.getLibrary().getKeywordDocumentation(keyword);
        return doc == null ? "" : doc;
    }

    private String getError(Throwable thrown) {
        final String simpleName = thrown.getClass().getSimpleName();
        if (genericExceptions.contains(simpleName) || isFlagSet("ROBOT_SUPPRESS_NAME", thrown)) {
            return thrown.getMessage() == null || thrown.getMessage().isEmpty() ? simpleName : thrown.getMessage();
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
            Map<Object, Object> newMap = new HashMap<>();
            for (Object key : oldMap.keySet())
                newMap.put(key, arraysToLists(oldMap.get(key)));
            return newMap;
        } else
            return arg;
    }

    private boolean illegalArgumentIn(Throwable t) {
        if (!Objects.nonNull(t) || t.getClass().equals(IllegalArgumentException.class)) {
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

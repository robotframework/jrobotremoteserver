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
package org.robotframework.remoteserver.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandLineHelper {

    private Map<String, Object> libraryMap = new HashMap<String, Object>();
    private boolean allowStop = true;
    private String host = null;
    private String error = null;
    private boolean helpRequested = false;
    private String[] args;
    private int idx = 0;
    private int port = 0;
    private boolean usedOldLibraryOption = false;

    public boolean getAllowStop() {
        return allowStop;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Map<String, Object> getLibraryMap() {
        return libraryMap;
    }

    public String getError() {
        return error;
    }

    public boolean getHelpRequested() {
        return helpRequested;
    }

    public String getUsage() {
        return "Usage: org.robotframework.remoteserver.RemoteServer options\n\n" + //
                "Options:\n" + //
                // //////////////////////////////////////////////////////////////////////////////
                "    -l --library classname[:path] library to serve and path to map to. Path\n" + //
                "                                  has a default value of /. The library option\n" + //
                "                                  may be repeated to serve multiple libraries\n" + //
                "    -p --port port                port to bind to, defaults to 0 (ephemeral)\n" + //
                "    -a --allowstop true|false     whether to allow remote stop\n" + //
                "    -H --host hostname            hostname of the interface to bind to\n" + //
                "    -h -? --help                  print this help message\n\n" + //
                " For backwards compatibility, one library can be added using the form\n" + //
                "   --library classname:port\n";
    }

    public CommandLineHelper(String[] clargs) {
        args = clargs;
        try {
            while (idx < args.length) {
                if (args[idx].equals("-l") || args[idx].equals("--library")) {
                    String[] parts = getValue("library").split(":", 3);
                    String className = parts[0];
                    String path = "/";
                    if (parts.length > 1) {
                        path = parts[1];
                    }
                    if (path.equals("") || path.matches("\\s+")) {
                        throw new RuntimeException("Missing path for library " + className);
                    }
                    String arg = null;
                    if (parts.length > 2) {
                        arg = parts[2];
                    }
                    putLibrary(path, className, arg);
                } else if (args[idx].equals("-p") || args[idx].equals("--port")) {
                    String portString = getValue("port");
                    setPort(portString);
                } else if (args[idx].equals("-H") || args[idx].equals("--host")) {
                    host = getValue("host");
                } else if (args[idx].equals("-a") || args[idx].equals("--allowstop")) {
                    String value = getValue("allowstop");
                    if (value.equalsIgnoreCase("false"))
                        allowStop = false;
                    else if (value.equalsIgnoreCase("true"))
                        allowStop = true;
                    else
                        throw new RuntimeException("Value for option allowstop must be true or false");
                } else if (Arrays.asList("-h", "-?", "--help").contains(args[idx])) {
                    helpRequested = true;
                    return;
                } else
                    throw new RuntimeException("Unkown option: " + args[idx]);
                idx++;
            }
            if (libraryMap.isEmpty())
                throw new RuntimeException("You must specify at least one library");
        } catch (Exception e) {
            error = e.getMessage();
        }
    }

    private String getValue(String name) {
        if (idx == args.length - 1 || (args[idx + 1].startsWith("-")))
            throw new RuntimeException("Missing value for option " + name);
        else
            return args[idx++ + 1];
    }

    private void putLibrary(String path, String className, String arg) {
        className = className.trim();
        if (path.matches("\\d+")) {
            setPort(path);
            usedOldLibraryOption = true;
            path = "/";
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load class with name " + className + ": " + e.toString());
        }
        Object library;
        if (arg == null) {
            try {
                library = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create library instance: " + e.toString());
            }
        } else {
            try {
                library = clazz.getDeclaredConstructor(String.class).newInstance(arg);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create library instance: " + e.toString() + " with argument " + arg);
            }
        }
        if (libraryMap.containsKey(path))
            throw new RuntimeException(String.format("Duplicate path [%s]", path));
        libraryMap.put(path, library);
    }

    private void setPort(String portString) {
        if (usedOldLibraryOption) {
            throw new RuntimeException(
                    "Cannot use the port option or use multiple libraries when specifying libraries in the form classname:port");
        }
        int port;
        try {
            port = Integer.valueOf(portString.trim());
            if (port < 1 || port > 65535)
                throw new Exception();
        } catch (Exception e) {
            throw new RuntimeException("Port must be 1-65535");
        }
        this.port = port;
    }

}

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
package org.robotframework.remoteserver.cli;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandLineHelper {

    private Map<Integer, String> libraryMap = new HashMap<Integer, String>();
    private boolean allowStop = true;
    private String host = null;
    private String error = null;
    private boolean helpRequested = false;
    private String[] args;
    private int idx = 0;

    public boolean getAllowStop() {
	return allowStop;
    }

    public String getHost() {
	return host;
    }

    public Map<Integer, String> getLibraryMap() {
	return libraryMap;
    }

    public String getError() {
	return error;
    }

    public boolean getHelpRequested() {
	return helpRequested;
    }

    public String getUsage() {
	return "Usage:  org.robotframework.remoteserver.RemoteServer options\n\n" + //
		"Options:\n" + //
		"    -l --library classname:port   test library to serve (can be repeated)\n" + //
		"    -a --allowstop true|false     whether to allow remote stop\n" + //
		"    -H --host hostname            hostname of the interface to bind to\n" + //
		"    -h -? --help                  print this help message";
    }

    public CommandLineHelper(String[] clargs) {
	args = clargs;
	try {
	    while (idx < args.length) {
		if (args[idx].equals("-l") || args[idx].equals("--library")) {
		    String[] parts = getValue("library").split(":");
		    if (parts.length != 2)
			throw new RuntimeException("Value for library must be in the format classname:port");
		    int port;
		    try {
			port = Integer.valueOf(parts[1].trim());
			if (port < 1 || port > 65535)
			    throw new Exception();
		    } catch (Exception e) {
			throw new RuntimeException("Port must be 1-65535");
		    }
		    if (libraryMap.containsKey(port))
			throw new RuntimeException("Cannot serve more than one library from the same port");
		    libraryMap.put(port, parts[0].trim());
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
}

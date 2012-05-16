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
package org.robotframework.remoteserver;

import java.util.HashMap;
import java.util.Map;

/**
 * XML-RPC server methods implementation for the Java generic remote server for
 * Robot Framework for calling remote libraries implemented Java.
 * 
 * Based on RobotFramework spec at
 * http://code.google.com/p/robotframework/wiki/RemoteLibrary
 * http://robotframework.googlecode
 * .com/svn/tags/robotframework-2.5.6/doc/userguide
 * /RobotFrameworkUserGuide.html#remote-library-interface http://robotframework
 * .googlecode.com/svn/tags/robotframework-2.5.6/doc/userguide/
 * RobotFrameworkUserGuide.html#dynamic-library-api
 * 
 * Uses Java reflection to serve the dynamically loaded remote Java class
 * library. You may alternatively modify this starting code base to natively
 * integrate your Java test library code into the server rather than load it
 * dynamically.
 * 
 * @author David Luu
 * 
 */
public class ServerMethods {

    /**
     * Get a list of RobotFramework keywords available in remote library for
     * use.
     * 
     * @return list of keywords in Java remote library
     */
    public String[] get_keyword_names() {
	try {
	    return RemoteServer.getLibrary().getKeywordNames();
	} catch (Throwable e) {
	    System.err.println(e);
	    return null;
	}
    }

    /**
     * Run specified Robot Framework keyword from remote server.
     * 
     * @param keyword
     *            Keyword class library method to run for Robot Framework.
     * @param args
     *            Arguments, if any, to pass to keyword method. An XML-RPC array
     *            or in Redstone library, a Java ArrayList.
     * @return RobotFramework specified data structure indicating pass/fail. An
     *         XML-RPC struct or in Redstone library, a Java HashMap.
     */
    public Map run_keyword(String keyword, Object[] args) {

	HashMap kr = new HashMap();
	try {
	    if (keyword.equalsIgnoreCase("stop_remote_server")) {
		// set return value
		kr.put("status", "PASS"); // RobotFramework spec for shutdown
		kr.put("return", 1);
		kr.put("error", "");
		kr.put("traceback", "");

		if (RemoteServer.getIsShutdownAllowed()) {
		    RemoteServer.stop();
		    kr.put("output", "NOTE: remote server shutting/shut down.");
		} else {
		    kr.put("output",
			    "NOTE: remote server not configured to allow remote shutdowns. Your request has been ignored.");
		    // in case RF spec changes to report failure in this case in
		    // future
		    // kr.put("status", "FAIL");
		    // kr.put("error","Remote server not configured to allow remote shutdowns. Your request has been ignored.");
		}
		return kr;
	    }
	    Object retObj = RemoteServer.getLibrary().runKeyword(keyword, args);

	    // TODO - check return type = array of some object,
	    // or simple types: int, String, boolean, etc.
	    // and process return value accordingly to send back to caller
	    // use .NET version of generic remote server as the model to follow
	    // http://code.google.com/p/sharprobotremoteserver/

	    // so for now, we do this...

	    // due to limitation of Java? (I think) in not being able to
	    // redirect
	    // standard (or stream) output from reflected/loaded library
	    // output will always be empty with this implementation. Until we
	    // can
	    // fix/optimize this deficiency.
	    kr.put("error", "");
	    kr.put("output", "");
	    kr.put("traceback", "");
	    kr.put("status", "PASS"); // always pass, if no exception, RF spec
	    if (retObj == null)
		kr.put("return", ""); // can't return null, so do this...
	    else
		kr.put("return", retObj);
	    return kr;
	} catch (Throwable e) {
	    e.printStackTrace();
	    kr.put("status", "FAIL");
	    kr.put("return", "");
	    kr.put("error", e.getMessage());
	    kr.put("output", e.getMessage());
	    String stktrc = "";
	    StackTraceElement[] st = e.getStackTrace();
	    for (int i = 0; i < st.length; i++) {
		stktrc += st.toString();
		kr.put("traceback", stktrc);
	    }
	    return kr;
	}
    }

    /**
     * Get list of arguments for specified Robot Framework keyword.
     * 
     * NOTE: Currently returns argument data type instead of name until we can
     * fix it.
     * 
     * @param keyword
     *            The keyword to get a list of arguments for.
     * @return A string array of arguments for the given keyword.
     */
    public String[] get_keyword_arguments(String keyword) {
	// TODO - figure out how to best to implement this
	// with reflection. Given a "keyword" method name,
	// lookup method in Java class library that's
	// loaded into the server, and return a string array
	// of arguments (names) that method takes.

	// my knowledge of Java reflection is limited,
	// so for now, we do this...
	if (keyword.equalsIgnoreCase("stop_remote_server")) {
	    return new String[0];
	}
	try {
	    return RemoteServer.getLibrary().getKeywordArguments(keyword);
	} catch (Throwable e) {
	    System.err.println(e);
	    return new String[0];
	}
    }

    /**
     * Get documentation for specified Robot Framework keyword.
     * 
     * NOTE: Tentatively done by getting class annotation info plus return value
     * of keyword method. Could enhance to parse Javadoc info, etc.
     * 
     * @param keyword
     *            The keyword to get documentation for.
     * @return A documentation string for the given keyword.
     */
    public String get_keyword_documentation(String keyword) {
	// TODO - figure out how to implement this.
	// Given a "keyword" method name, lookup method's
	// documentation, and return as string.
	// Use Javadoc API? Use doclet API?
	// Use Java annotations?
	// Look into how Robot Framework (non-remote)
	// Java/Jython libraries implement this
	// (with annotations, I believe)

	// my knowledge of Java reflection +
	// Java documentation is limited,
	// so for now, we do this...
	if (keyword.equalsIgnoreCase("stop_remote_server")) {
	    String doc = "Remotely shut down remote server/library w/ Robot Framework keyword.\n\n";
	    doc += "If server is configured to not allow remote shutdown, keyword 'request' is ignored by server.\n\n";
	    doc += "Always returns status of PASS with return value of 1. Output value contains helpful info and may indicate whether remote shut down is allowed or not.";
	    return doc;
	}
	try {
	    return RemoteServer.getLibrary().getKeywordDocumentation(keyword);
	} catch (Throwable e) {
	    System.err.println(e);
	    return "";
	}
    }
}

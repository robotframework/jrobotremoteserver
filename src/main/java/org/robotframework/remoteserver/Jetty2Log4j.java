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

import org.eclipse.jetty.util.log.Logger;
import org.apache.log4j.Level;

/**
 * Jetty logger that redirects directly to Log4j without needing slf4j on the classpath. This way users do not need to
 * add more dependencies than jrobotremoteserver to get a unified logging solution.
 */
public class Jetty2Log4j implements Logger {

    private org.apache.log4j.Logger logger;
    private Level configuredLevel;

    public Jetty2Log4j() {
	this("org.eclipse.jetty.util.log");
    }

    public Jetty2Log4j(String name) {
	logger = org.apache.log4j.Logger.getLogger(name);
    }

    public String getName() {
	return logger.getName();
    }

    public void warn(String msg, Object... args) {
	logger.warn(format(msg, args));
    }

    public void warn(Throwable thrown) {
	logger.warn("", thrown);
    }

    public void warn(String msg, Throwable thrown) {
	logger.warn(msg, thrown);
    }

    public void info(String msg, Object... args) {
	logger.info(format(msg, args));
    }

    public void info(Throwable thrown) {
	logger.info(null, thrown);
    }

    public void info(String msg, Throwable thrown) {
	logger.info(msg, thrown);
    }

    public boolean isDebugEnabled() {
	return false;
    }

    public void setDebugEnabled(boolean enabled) {
	if (enabled) {
	    configuredLevel = logger.getLevel();
	    logger.setLevel(Level.DEBUG);
	} else {
	    logger.setLevel(configuredLevel);
	}
    }

    public void debug(String msg, Object... args) {
	logger.debug(format(msg, args));
    }

    public void debug(Throwable thrown) {
	logger.debug("", thrown);
    }

    public void debug(String msg, Throwable thrown) {
	logger.debug(msg, thrown);
    }

    public Logger getLogger(String name) {
	return new Jetty2Log4j(name);
    }

    public void ignore(Throwable ignored) {
	logger.trace("", ignored);
    }

    private String format(String msg, Object... args) {
	msg = String.valueOf(msg); // Avoids NPE
	String braces = "{}";
	StringBuilder builder = new StringBuilder();
	int start = 0;
	for (Object arg : args) {
	    int bracesIndex = msg.indexOf(braces, start);
	    if (bracesIndex < 0) {
		builder.append(msg.substring(start));
		builder.append(" ");
		builder.append(arg);
		start = msg.length();
	    } else {
		builder.append(msg.substring(start, bracesIndex));
		builder.append(String.valueOf(arg));
		start = bracesIndex + braces.length();
	    }
	}
	builder.append(msg.substring(start));
	return builder.toString();
    }
}

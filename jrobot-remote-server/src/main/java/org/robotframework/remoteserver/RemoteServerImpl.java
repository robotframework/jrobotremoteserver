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
package org.robotframework.remoteserver;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.robotframework.remoteserver.library.RemoteLibrary;
import org.robotframework.remoteserver.servlet.RemoteServerServlet;

/**
 * Remote server for Robot Framework implemented in Java. Takes one or more test
 * libraries and exposes their methods via XML-RPC using an embedded web server.
 * To use a different web server, use
 * {@link org.robotframework.remoteserver.servlet.RemoteServerServlet} instead.
 *
 * @see <a href="https://github.com/ombre42/jrobotremoteserver/wiki">jrobotremoteserver wiki</a>
 * @see <a href="http://code.google.com/p/robotframework/wiki/RemoteLibrary">Remote Library wiki page</a>
 * @see <a href="http://code.google.com/p/robotframework/wiki/UserGuide">User
 * Guide for Robot Framework</a>
 * @see <a href="http://xmlrpc.scripting.com/spec.html">XML-RPC
 * Specification</a>
 */
public class RemoteServerImpl implements RemoteServer {

    private static Log log = LogFactory.getLog(RemoteServer.class);
    protected Server server = new Server();
    private RemoteServerServlet servlet = new RemoteServerServlet();
    private SelectChannelConnector connector = new SelectChannelConnector();

    public RemoteServerImpl() {
        connector.setName("jrobotremoteserver");
        server.setConnectors(new Connector[] {connector});
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", false, false);
        servletContextHandler.addServlet(new ServletHolder(servlet), "/");
    }

    /**
     * Returns the actual port the server is listening on.
     *
     * @return The actual port the server's connector is listening on or -1 if
     * it has not been opened, or -2 if it has been closed.
     */
    public int getPort() {
        return connector.getLocalPort();
    }

    /**
     * Sets the port to listen on.
     *
     * @param port The port to listen on for connections or 0 if any available
     *             port may be used. Defaults port is 0.
     */
    public void setPort(int port) {
        connector.setPort(port);
    }

    /**
     * Returns <code>true</code> if this server allows remote stopping.
     *
     * @return <code>true</code> if this server allows remote stopping
     */
    public boolean getAllowStop() {
        return servlet.getAllowStop();
    }

    /**
     * Allow or disallow stopping the server remotely.
     *
     * @param allowed <code>true</code> to allow stopping the server remotely
     */
    public void setAllowStop(boolean allowed) {
        servlet.setAllowStop(allowed);
    }

    /**
     * Returns the hostname set with {@link #setHost(String)}.
     *
     * @return the hostname set with {@link #setHost(String)}
     */
    public String getHost() {
        return connector.getHost();
    }

    /**
     * Set the hostname of the interface to bind to. Usually not needed and
     * determined automatically. For exotic network configuration, network with
     * VPN, specifying the host might be necessary.
     *
     * @param hostName the hostname or address representing the interface to which
     *                 all connectors will bind, or null for all interfaces.
     */
    public void setHost(String hostName) {
        connector.setHost(hostName);
    }

    @Override public RemoteLibrary putLibrary(String path, Object library) {
        RemoteLibrary oldLibrary = servlet.putLibrary(path, library);
        String name = servlet.getLibraryMap().get(path).getName();
        log.info(String.format("Mapped path %s to library %s.", path, name));
        return oldLibrary;
    }

    @Override public RemoteLibrary removeLibrary(String path) {
        return servlet.removeLibrary(path);
    }

    @Override public Map<String, RemoteLibrary> getLibraryMap() {
        return servlet.getLibraryMap();
    }

    @Override public void stop(int timeoutMS) throws Exception {
        log.info("Robot Framework remote server stopping");
        if (timeoutMS > 0) {
            server.setGracefulShutdown(timeoutMS);
            Thread stopper = new Thread() {

                @Override public void run() {
                    try {
                        server.stop();
                    } catch (Throwable e) {
                        log.error(String.format("Failed to stop the server: %s", e.getMessage()), e);
                    }
                }
            };
            stopper.start();
        } else {
            server.stop();
        }
    }

    @Override public void stop() throws Exception {
        stop(0);
    }

    @Override public void start() throws Exception {
        log.info("Robot Framework remote server starting");
        server.start();
        log.info(String.format("Robot Framework remote server started on port %d.", getPort()));
    }
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

    private static final ThreadLocal<Integer> port = new ThreadLocal<Integer>();

    private static final ConcurrentHashMap<Integer, RemoteServer> remoteServerMap = new ConcurrentHashMap<Integer, RemoteServer>();

    protected static void setPort(Integer rPort) {
	port.set(rPort);
    }

    protected static RemoteServer getRemoteServer() {
	return remoteServerMap.get(port.get());
    }

    protected static void addRemoteServer(RemoteServer server, Set<Integer> ports) {
	synchronized (remoteServerMap) {
	    for (Integer port : ports) {
		if (remoteServerMap.containsKey(port))
		    throw new RuntimeException(String.format("Another RemoteServer has claimed port %d", port));
	    }
	    for (Integer port : ports) {
		remoteServerMap.put(port, server);
	    }
	}
    }

    protected static void removeRemoteServer(RemoteServer server) {
	List<Integer> remove = new ArrayList<Integer>();
	for (Integer port : remoteServerMap.keySet())
	    if (remoteServerMap.get(port) == server)
		remove.add(port);
	for (Integer port : remove)
	    remoteServerMap.remove(port);
    }

    protected static IRemoteLibrary getLibrary() {
	return getRemoteServer().getLibrary(port.get());
    }

}

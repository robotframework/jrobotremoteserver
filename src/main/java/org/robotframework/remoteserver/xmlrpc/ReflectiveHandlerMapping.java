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
package org.robotframework.remoteserver.xmlrpc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;

public class ReflectiveHandlerMapping extends AbstractReflectiveHandlerMapping {

    /**
     * Removes the prefixes from all keys in this handler mapping assuming a String was used as the key and period was
     * used as a separator. Example: AccountsReceivable.Billing.getInvoice to getInvoice
     */
    @SuppressWarnings("unchecked")
    public void removePrefixes() {
	Map<String, Object> newHandlerMap = new HashMap<String, Object>();
	for (Entry<String, Object> entry : (Set<Entry<String, Object>>) this.handlerMap.entrySet()) {
	    String newKey = (String) entry.getKey();
	    if (entry.getKey() instanceof String) {
		String key = (String) entry.getKey();
		if (key.contains(".")) {
		    newKey = key.substring(key.lastIndexOf(".") + 1);
		}
	    }
	    newHandlerMap.put(newKey, entry.getValue());
	}
	this.handlerMap = newHandlerMap;
    }

    /**
     * Adds handlers for the given object to the mapping. The handlers are build by invoking
     * {@link #registerPublicMethods(String, Class)}.
     * 
     * @param pKey
     *            The class key, which is passed to {@link #registerPublicMethods(String, Class)}.
     * @param pClass
     *            Class, which is responsible for handling the request.
	 * @throws XmlRpcException Performing the request failed.
     */
    public void addHandler(String pKey, Class<?> pClass) throws XmlRpcException {
	registerPublicMethods(pKey, pClass);
    }
}

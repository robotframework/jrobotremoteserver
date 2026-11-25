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

import java.util.Iterator;

import org.apache.xmlrpc.common.TypeFactory;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.serializer.ObjectArraySerializer;
import org.apache.xmlrpc.serializer.SerializerHandler;
import org.apache.xmlrpc.serializer.TypeSerializer;
import org.xml.sax.SAXException;

public class IterableSerializer extends ObjectArraySerializer {

	private final TypeFactory typeFactory;
	private final XmlRpcStreamConfig config;

	public IterableSerializer(TypeFactory pTypeFactory, XmlRpcStreamConfig pConfig) {
		super(pTypeFactory, pConfig);
		typeFactory = pTypeFactory;
		config = pConfig;
	}

	@Override
	protected void writeData(SerializerHandler pHandler, Object pObject)
			throws SAXException {

		Iterable<?> obj = (Iterable<?>) pObject;
		Iterator<?> iter = obj.iterator();

		while (iter.hasNext()) {
			Object value = iter.next();
			TypeSerializer serializer = typeFactory.getSerializer(config, value);
			serializer.write(pHandler, value);
		}
	}
}
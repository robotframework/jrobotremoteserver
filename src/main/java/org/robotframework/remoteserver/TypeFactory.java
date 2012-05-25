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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.common.TypeFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcController;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.serializer.StringSerializer;
import org.apache.xmlrpc.serializer.TypeSerializer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TypeFactory extends TypeFactoryImpl {

    private Log log = LogFactory.getLog(TypeFactory.class);

    private static final StringSerializer nullSerializer = new StringSerializer() {
	@Override
	public void write(ContentHandler pHandler, Object pObject) throws SAXException {
	    write(pHandler, null, "");
	}
    };

    public TypeFactory(XmlRpcController pController) {
	super(pController);
    }

    @Override
    public TypeSerializer getSerializer(XmlRpcStreamConfig pConfig, Object pObject) throws SAXException {
	// TODO: handle Iterator & arbitrary objects & objects containing null
	if (pObject == null) {
	    log.debug("Converting null to \"\"");
	    return nullSerializer;
	}
	return super.getSerializer(pConfig, pObject);
    }
}

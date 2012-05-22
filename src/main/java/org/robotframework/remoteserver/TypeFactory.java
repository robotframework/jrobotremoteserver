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

    private static Log log = LogFactory.getLog(TypeFactory.class);

    private static final StringSerializer nullSerializer = new StringSerializer() {
	@Override
	public void write(ContentHandler pHandler, Object pObject) throws SAXException {
	    System.err.println("Converting null to empty string");
	    log.info("Converting null to empty string");
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
	    return nullSerializer;
	}
	return super.getSerializer(pConfig, pObject);
    }
}

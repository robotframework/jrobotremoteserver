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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ws.commons.util.NamespaceContextImpl;
import org.apache.xmlrpc.common.TypeFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcController;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.parser.TypeParser;
import org.apache.xmlrpc.serializer.BooleanSerializer;
import org.apache.xmlrpc.serializer.ByteArraySerializer;
import org.apache.xmlrpc.serializer.DoubleSerializer;
import org.apache.xmlrpc.serializer.I4Serializer;
import org.apache.xmlrpc.serializer.ListSerializer;
import org.apache.xmlrpc.serializer.ObjectArraySerializer;
import org.apache.xmlrpc.serializer.TypeSerializer;
import org.apache.xmlrpc.serializer.TypeSerializerImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TypeFactory extends TypeFactoryImpl {

    private static final TypeSerializer STRING_SERIALIZER = new StringSerializer();
    private static final TypeSerializer I4_SERIALIZER = new I4Serializer();
    private static final TypeSerializer DOUBLE_SERIALIZER = new DoubleSerializer();
    private static final TypeSerializer BOOLEAN_SERIALIZER = new BooleanSerializer();
    private static final TypeSerializer NULL_SERIALIZER = new org.apache.xmlrpc.serializer.StringSerializer() {
        @Override
        public void write(ContentHandler pHandler, Object pObject) throws SAXException {
            write(pHandler, null, "");
        }
    };
    private static final TypeSerializer CHAR_ARRAY_SERIALIZER = new TypeSerializerImpl() {
        public void write(ContentHandler pHandler, Object pObject) throws SAXException {
            char[] chars = (char[]) pObject;
            write(pHandler, null, chars);
        }
    };
    private TypeSerializer primitiveArraySerializer;
    private static final TypeParser BYTE_ARRAY_PARSER = new ByteArrayToStringParser();

    public TypeFactory(XmlRpcController pController) {
        super(pController);
    }

    public TypeSerializer getSerializer(XmlRpcStreamConfig pConfig, Object pObject) throws SAXException {
        if (pObject == null)
            return NULL_SERIALIZER;
        else if (pObject instanceof String)
            return STRING_SERIALIZER;
        else if (pObject instanceof Integer || pObject instanceof Short || pObject instanceof Byte)
            return I4_SERIALIZER;
        else if (pObject instanceof Boolean)
            return BOOLEAN_SERIALIZER;
        else if (pObject instanceof Double || pObject instanceof Float)
            return DOUBLE_SERIALIZER;
        else if (pObject instanceof Object[])
            return new ObjectArraySerializer(this, pConfig);
        else if (pObject instanceof List)
            return new ListSerializer(this, pConfig);
        else if (pObject instanceof Map)
            return new MapSerializer(this, pConfig);
        else if (pObject instanceof Iterable)
            return new IterableSerializer(this, pConfig);
        else if (pObject instanceof char[])
            return CHAR_ARRAY_SERIALIZER;
        else if (pObject.getClass().isArray()) { // object[] & char[] handled
                                                 // before this
            primitiveArraySerializer = new ObjectArraySerializer(this, pConfig) {
                @Override
                protected void writeData(ContentHandler pHandler, Object pObject) throws SAXException {
                    Object[] array;
                    if (pObject instanceof byte[])
                        array = ArrayUtils.toObject((byte[]) pObject);
                    else if (pObject instanceof short[])
                        array = ArrayUtils.toObject((short[]) pObject);
                    else if (pObject instanceof int[])
                        array = ArrayUtils.toObject((int[]) pObject);
                    else if (pObject instanceof long[])
                        array = ArrayUtils.toObject((long[]) pObject);
                    else if (pObject instanceof float[])
                        array = ArrayUtils.toObject((float[]) pObject);
                    else if (pObject instanceof double[])
                        array = ArrayUtils.toObject((double[]) pObject);
                    else if (pObject instanceof boolean[])
                        array = ArrayUtils.toObject((boolean[]) pObject);
                    else
                        // should never happen
                        throw new SAXException(String.format("Array of type %s[] not handled!", pObject.getClass()
                                .getComponentType().getName()));
                    super.writeData(pHandler, array);
                }
            };
            return primitiveArraySerializer;
        } else
            return STRING_SERIALIZER;
    }

    @Override
    public TypeParser getParser(XmlRpcStreamConfig pConfig, NamespaceContextImpl pContext, String pURI,
            String pLocalName) {
        if (ByteArraySerializer.BASE_64_TAG.equals(pLocalName)) {
            return BYTE_ARRAY_PARSER;
        }
        return super.getParser(pConfig, pContext, pURI, pLocalName);
    }

}

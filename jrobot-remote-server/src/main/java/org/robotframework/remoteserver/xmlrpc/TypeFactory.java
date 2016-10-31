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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
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

        @Override public void write(ContentHandler pHandler, Object pObject) throws SAXException {
            write(pHandler, null, "");
        }
    };
    private static final TypeSerializer CHAR_ARRAY_SERIALIZER = new TypeSerializerImpl() {

        public void write(ContentHandler pHandler, Object pObject) throws SAXException {
            char[] chars = (char[]) pObject;
            write(pHandler, null, chars);
        }
    };
    private static final TypeParser BYTE_ARRAY_PARSER = new ByteArrayToStringParser();

    public TypeFactory(XmlRpcController pController) {
        super(pController);
    }

    public static Byte[] toObject(final byte[] primitives) {
        final Byte[] bytes = new Byte[primitives.length];
        Arrays.setAll(bytes, n -> primitives[n]);
        return bytes;
    }

    public static Short[] toObject(final short[] primitives) {
        final Short[] bytes = new Short[primitives.length];
        Arrays.setAll(bytes, n -> primitives[n]);
        return bytes;
    }

    public static Integer[] toObject(final int[] primitives) {
        return IntStream.of(primitives).boxed().toArray(Integer[]::new);
    }

    public static Long[] toObject(final long[] primitives) {
        return LongStream.of(primitives).boxed().toArray(Long[]::new);
    }

    public static Float[] toObject(final float[] primitives) {
        final Float[] bytes = new Float[primitives.length];
        Arrays.setAll(bytes, n -> primitives[n]);
        return bytes;
    }

    public static Double[] toObject(final double[] primitives) {
        return DoubleStream.of(primitives).boxed().toArray(Double[]::new);
    }

    public static Boolean[] toObject(final boolean[] primitives) {
        final Boolean[] bytes = new Boolean[primitives.length];
        Arrays.setAll(bytes, n -> primitives[n]);
        return bytes;
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
        else if (pObject.getClass().isArray()) {
            return new ObjectArraySerializer(TypeFactory.this, pConfig) {

                @Override protected void writeData(ContentHandler pHandler, Object pObject1) throws SAXException {
                    Object[] array;
                    if (pObject1 instanceof byte[])
                        array = toObject((byte[]) pObject1);
                    else if (pObject1 instanceof short[])
                        array = toObject((short[]) pObject1);
                    else if (pObject1 instanceof int[])
                        array = toObject((int[]) pObject1);
                    else if (pObject1 instanceof long[])
                        array = toObject((long[]) pObject1);
                    else if (pObject1 instanceof float[])
                        array = toObject((float[]) pObject1);
                    else if (pObject1 instanceof double[])
                        array = toObject((double[]) pObject1);
                    else if (pObject1 instanceof boolean[])
                        array = toObject((boolean[]) pObject1);
                    else
                        // should never happen
                        throw new SAXException(String.format("Array of type %s[] not handled!",
                                pObject1.getClass().getComponentType().getName()));
                    super.writeData(pHandler, array);
                }
            };
        } else
            return STRING_SERIALIZER;
    }

    @Override public TypeParser getParser(XmlRpcStreamConfig pConfig, NamespaceContextImpl pContext, String pURI,
            String pLocalName) {
        if (ByteArraySerializer.BASE_64_TAG.equals(pLocalName)) {
            return BYTE_ARRAY_PARSER;
        }
        return super.getParser(pConfig, pContext, pURI, pLocalName);
    }

}

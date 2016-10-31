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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;
import org.apache.ws.commons.util.Base64;
import org.apache.ws.commons.util.Base64.Encoder;
import org.apache.xmlrpc.serializer.TypeSerializerImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class StringSerializer extends TypeSerializerImpl {

    public static final String BASE_64_TAG = "base64";
    private static final Pattern pattern = Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]");

    public void write(ContentHandler pHandler, Object pObject) throws SAXException {
        String value = pObject.toString();
        if (pattern.matcher(value).find()) {
            pHandler.startElement("", VALUE_TAG, VALUE_TAG, ZERO_ATTRIBUTES);
            pHandler.startElement("", BASE_64_TAG, BASE_64_TAG, ZERO_ATTRIBUTES);
            try {
                byte[] buffer = value.getBytes("UTF-8");
                if (buffer.length > 0) {
                    char[] charBuffer = new char[buffer.length >= 1024 ? 1024 : ((buffer.length + 3) / 4) * 4];
                    Encoder encoder = new Base64.SAXEncoder(charBuffer, 0, null, pHandler);
                    try {
                        encoder.write(buffer, 0, buffer.length);
                        encoder.flush();
                    } catch (Base64.SAXIOException e) {
                        throw e.getSAXException();
                    } catch (IOException e) {
                        throw new SAXException(e);
                    }
                }
                pHandler.endElement("", BASE_64_TAG, BASE_64_TAG);
                pHandler.endElement("", VALUE_TAG, VALUE_TAG);
            } catch (UnsupportedEncodingException e1) {
                // TODO log exception
            }
        } else {
            write(pHandler, null, value);
        }
    }

}

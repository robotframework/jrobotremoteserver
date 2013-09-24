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
/* This code is derived from JavalibCore 
 * Copyright 2008 Nokia Siemens Networks Oyj
 */
package org.robotframework.remoteserver.javalib;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.robotframework.javalib.beans.annotation.IKeywordExtractor;
import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.javalib.reflection.IKeywordInvoker;
import org.robotframework.javalib.reflection.KeywordInvoker;

public class SimpleKeywordExtractor implements IKeywordExtractor<Keyword> {

    public Map<String, Keyword> extractKeywords(Object keywordBean) {
	Map<String, Keyword> extractedKeywords = new HashMap<String, Keyword>();
        Method[] methods = keywordBean.getClass().getMethods();

        for (final Method method : methods) {
            if (method.getDeclaringClass() != Object.class && Modifier.isPublic(method.getModifiers())) {
                Keyword keyword = createKeyword(keywordBean, method);
                String methodName = method.getName();
                if (extractedKeywords.containsKey(methodName))
        	    throw new RuntimeException("Overloaded method with name '" + methodName + "' found!");
                extractedKeywords.put(method.getName(), keyword);
            }
        }
        return extractedKeywords;
    }
    
    IKeywordInvoker createKeywordInvoker(Object keywordBean, Method method) {
        return new KeywordInvoker(keywordBean, method);
    }

    private Keyword createKeyword(Object keywordBean, Method method) {
        IKeywordInvoker keywordInvoker = createKeywordInvoker(keywordBean, method);
        return createKeyword(keywordInvoker);
    }

    private Keyword createKeyword(final IKeywordInvoker keywordInvoker) {
        return new Keyword() {
            public Object execute(Object[] arguments) {
                return keywordInvoker.invoke(arguments);
            }
        };
    }
}

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
/* This code is derived from JavalibCore
 * Copyright 2008 Nokia Siemens Networks Oyj
 */
package org.robotframework.remoteserver.javalib;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.robotframework.javalib.factory.KeywordFactory;
import org.robotframework.javalib.library.KeywordDocumentationRepository;
import org.robotframework.javalib.library.KeywordFactoryBasedLibrary;

public class SingleClassLibrary extends KeywordFactoryBasedLibrary<OverloadableKeyword> implements
        KeywordDocumentationRepository {

    private KeywordFactory<OverloadableKeyword> keywordFactory;
    private Object keywordBean;

    public SingleClassLibrary(Object keywordBean) {
        this.keywordBean = keywordBean;
    }

    @Override
    protected KeywordFactory<OverloadableKeyword> createKeywordFactory() {
        if (keywordFactory == null) {
            keywordFactory = new SimpleKeywordFactory(keywordBean);
        }
        return keywordFactory;
    }

    public Object runKeyword(String keywordName, List args) {
        try {
            return super.runKeyword(keywordName, args);
        } catch (RuntimeException e) {
            throw retrieveInnerException(e);
        }
    }

    @Override
    public List<String> getKeywordArguments(String keywordName) {
        return createKeywordFactory().createKeyword(keywordName).getArguments();
    }

    @Override
    public String getKeywordDocumentation(String keywordName) {
        return "";
    }

    private RuntimeException retrieveInnerException(RuntimeException e) {
        Throwable cause = e.getCause();
        if (InvocationTargetException.class.equals(cause.getClass())) {
            Throwable original = cause.getCause();
            return new RuntimeException(original.getMessage(), original);
        }
        return e;
    }

}

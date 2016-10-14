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
package org.robotframework.remoteserver.library;

import com.google.common.base.Preconditions;
import java.util.Map;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.robotframework.javalib.factory.KeywordFactory;
import org.robotframework.javalib.library.KeywordFactoryBasedLibrary;
import org.robotframework.remoteserver.RemoteServer;
import org.robotframework.remoteserver.keywords.OverloadedKeyword;
import org.robotframework.remoteserver.keywords.OverloadedKeywordExtractor;
import org.robotframework.remoteserver.keywords.OverloadedKeywordFactory;

@RobotKeywords public abstract class AbstractClassLibrary extends KeywordFactoryBasedLibrary<OverloadedKeyword>
        implements RemoteLibrary {

    private KeywordFactory<OverloadedKeyword> keywordFactory;

    protected AbstractClassLibrary(RemoteServer server) {
        Preconditions.checkNotNull(server).putLibrary("/" + getURI().trim().replace(" ", "_"), this);
    }

    @Override protected synchronized KeywordFactory<OverloadedKeyword> createKeywordFactory() {
        if (keywordFactory == null) {
            keywordFactory = new OverloadedKeywordFactory(this, OverloadedKeywordExtractor.createInstance());
        }
        return keywordFactory;
    }

    @Override public synchronized Object runKeyword(String keywordName, Object[] args, Map<String, Object> kwargs) {
        if (kwargs != null && !kwargs.isEmpty()) {
            throw new RuntimeException("Keyword arguments not yet supported for static API libraries.");
        }
        return runKeyword(keywordName, args);
    }

    @Override public synchronized String[] getKeywordArguments(String keywordName) {
        return createKeywordFactory().createKeyword(keywordName).getArgumentNames();
    }

    @Override public synchronized String getKeywordDocumentation(String keywordName) {
        return createKeywordFactory().createKeyword(keywordName).getDocumentation();
    }

    @Override public abstract String getURI();

    @RobotKeyword public void libraryCleanup() {
        close();
    }
}

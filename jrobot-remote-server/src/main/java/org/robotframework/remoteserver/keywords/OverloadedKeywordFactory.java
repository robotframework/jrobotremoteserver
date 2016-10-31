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
package org.robotframework.remoteserver.keywords;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.robotframework.javalib.factory.KeywordFactory;
import org.robotframework.javalib.util.IKeywordNameNormalizer;
import org.robotframework.javalib.util.KeywordNameNormalizer;
import org.robotframework.remoteserver.library.RemoteLibrary;

public class OverloadedKeywordFactory implements KeywordFactory<OverloadedKeyword> {

    private final KeywordExtractor<OverloadedKeyword> extractor;
    private final Map<String, OverloadedKeyword> keywords = new HashMap<>();
    private final IKeywordNameNormalizer keywordNameNormalizer = new KeywordNameNormalizer();

    public OverloadedKeywordFactory(RemoteLibrary keywordBean, KeywordExtractor<OverloadedKeyword> extractor) {
        this.extractor = Objects.requireNonNull(extractor);
        extractKeywordsFromKeywordBean(Objects.requireNonNull(keywordBean));
    }

    @Override public OverloadedKeyword createKeyword(String keywordName) {
        return keywords.get(keywordNameNormalizer.normalize(keywordName));
    }

    @Override public String[] getKeywordNames() {
        return keywords.keySet().toArray(new String[keywords.size()]);
    }

    protected void extractKeywordsFromKeywordBean(RemoteLibrary keywordBean) {
        Map<String, OverloadedKeyword> extractedKeywords = extractor.extractKeywords(keywordBean);
        for (String keywordName : extractedKeywords.keySet()) {
            if (keywords.containsKey(keywordNameNormalizer.normalize(keywordName))) {
                throw new RuntimeException("Two keywords with name '" + keywordName + "' found!");
            }
            keywords.put(keywordNameNormalizer.normalize(keywordName), extractedKeywords.get(keywordName));
        }
    }
}

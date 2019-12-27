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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.robotframework.javalib.beans.annotation.IKeywordExtractor;
import org.robotframework.javalib.factory.KeywordFactory;
import org.robotframework.javalib.util.IKeywordNameNormalizer;
import org.robotframework.javalib.util.KeywordNameNormalizer;

public class SimpleKeywordFactory implements KeywordFactory<OverloadableKeyword> {
    private Map<String, OverloadableKeyword> keywords = new HashMap<String, OverloadableKeyword>();
    private IKeywordNameNormalizer keywordNameNormalizer = new KeywordNameNormalizer();
    private List<String> keywordNames = new ArrayList<String>();

    public SimpleKeywordFactory(Object keywordBean) {
        extractKeywordsFromKeywordBean(keywordBean);
    }

    public OverloadableKeyword createKeyword(String keywordName) {
        String normalizedKeywordName = keywordNameNormalizer.normalize(keywordName);
        return keywords.get(normalizedKeywordName);
    }

    public List<String> getKeywordNames() {
        return keywordNames;
    }

    protected void extractKeywordsFromKeywordBean(Object keywordBean) {
        IKeywordExtractor<OverloadableKeyword> keywordExtractor = createKeywordExtractor();
        Map<String, OverloadableKeyword> extractedKeywords = keywordExtractor.extractKeywords(keywordBean);
        addKeywordNames(extractedKeywords);
        addKeywords(extractedKeywords);
    }

    IKeywordExtractor<OverloadableKeyword> createKeywordExtractor() {
        return new SimpleKeywordExtractor();
    }

    private void addKeywords(Map<String, OverloadableKeyword> extractedKeywords) {
        for (String keywordName : extractedKeywords.keySet()) {
            handleDuplicateKeywordNames(keywordName);
            keywords.put(keywordNameNormalizer.normalize(keywordName), extractedKeywords.get(keywordName));
        }
    }

    private void handleDuplicateKeywordNames(String keywordName) {
        if (keywords.containsKey(keywordNameNormalizer.normalize(keywordName))) {
            throw new RuntimeException("Two keywords with name '" + keywordName + "' found!");
        }
    }

    private void addKeywordNames(Map<String, OverloadableKeyword> extractedKeywords) {
        keywordNames.addAll(extractedKeywords.keySet());
    }
}

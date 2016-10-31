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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;
import org.robotframework.javalib.util.IKeywordNameNormalizer;
import org.robotframework.javalib.util.KeywordNameNormalizer;
import org.robotframework.remoteserver.library.RemoteLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverloadedKeywordExtractor implements KeywordExtractor<OverloadedKeyword> {

    protected static final Logger LOG = LoggerFactory.getLogger(OverloadedKeywordExtractor.class.getName());
    private static OverloadedKeywordExtractor singleton;

    protected OverloadedKeywordExtractor() {
    }

    public static synchronized OverloadedKeywordExtractor createInstance() {
        if (singleton == null) {
            singleton = new OverloadedKeywordExtractor();
        }
        return singleton;
    }

    public static Stream<Method> getMethods(Class<?> obj) {
        if (obj == null)
            return Stream.empty();
        Stream<Method> methods = Stream.of(obj.getMethods());
        for (Class<?> i : obj.getInterfaces()) {
            methods = Stream.concat(methods, getMethods(i));
        }
        return Stream.concat(methods, getMethods(obj.getSuperclass()));
    }

    @Override public Map<String, OverloadedKeyword> extractKeywords(final RemoteLibrary keywordBean) {
        Objects.requireNonNull(keywordBean);
        LOG.warn("Extracting {}", keywordBean.getURI());
        IKeywordNameNormalizer keywordNameNormalizer = new KeywordNameNormalizer();
        final Map<String, OverloadedKeyword> overloadableKeywords = new HashMap<>();
        final Set<String> interfaceKeywords = new HashSet<>(), interfaceKeywordsOverload = new HashSet<>();
        getMethods(keywordBean.getClass()).forEach(method -> {
            if (method.isAnnotationPresent(RobotKeyword.class)) {
                interfaceKeywords.add(method.getName());
                LOG.warn("{} Detected Keyword  {}", method.getName(),
                        keywordNameNormalizer.normalize(method.getName()));
            } else if (method.isAnnotationPresent(RobotKeywordOverload.class)) {
                interfaceKeywordsOverload.add(method.getName());
                LOG.warn("{} Detected KeywordOverload  {}", method.getName(),
                        keywordNameNormalizer.normalize(method.getName()));
            }
        });
        Arrays.stream(keywordBean.getClass().getMethods())
                .filter(m -> (m.isAnnotationPresent(RobotKeyword.class) || interfaceKeywords.contains(m.getName()))
                        && !overloadableKeywords.containsKey(m.getName()))
                .forEach(m -> overloadableKeywords.put(m.getName(), new OverloadedKeywordImpl(keywordBean, m)));
        Arrays.stream(keywordBean.getClass().getMethods())
                .filter(m -> overloadableKeywords.containsKey(m.getName()) && (
                        m.isAnnotationPresent(RobotKeywordOverload.class) || interfaceKeywordsOverload.contains(
                                m.getName())))
                .forEach(m -> overloadableKeywords.get(m.getName()).addOverload(m));
        return overloadableKeywords;
    }

}

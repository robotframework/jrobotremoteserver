package org.robotframework.remoteserver.keywords;

import java.util.Map;
import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.remoteserver.library.RemoteLibrary;

public interface KeywordExtractor<T extends Keyword> {

    Map<String, T> extractKeywords(RemoteLibrary keywordBean);
}

package org.robotframework.remoteserver.keywords;

import org.robotframework.javalib.keyword.DocumentedKeyword;

public interface CheckedKeyword extends DocumentedKeyword {

    boolean canExecute(Object[] args);

}

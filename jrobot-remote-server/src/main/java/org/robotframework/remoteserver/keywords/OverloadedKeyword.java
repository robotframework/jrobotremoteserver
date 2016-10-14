package org.robotframework.remoteserver.keywords;

import java.lang.reflect.Method;
import org.robotframework.javalib.keyword.DocumentedKeyword;

public interface OverloadedKeyword extends DocumentedKeyword {

    void addOverload(Method method);
}

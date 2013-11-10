package org.robotframework.remoteserver.testlibraries;

public class ConflictingOverloadVariableArguments
{
    public void myKeyword() {}
    public void myKeyword(float[] numbers) {}
}

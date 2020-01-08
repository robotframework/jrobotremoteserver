*** Settings ***
Suite Setup    Setup suite
Suite Teardown    Teardown suite
#Test Setup    Import remote library
Library    Process

*** Variables ***
${PORT}              8270

*** Keywords ***
Setup suite
    ${process}    Start Process    java    -cp    ${maven.test.classpath}:${maven.runtime.classpath}    org.robotframework.remoteserver.RemoteServer
    ...    --library    org.robotframework.examplelib.FullDynamic:/FullDynamic
    ...    --library    org.robotframework.examplelib.MinDynamic:/MinDynamic
    ...    --library    org.robotframework.examplelib.Static:/Static
    ...    --library    org.robotframework.examplelib.MinDynamicKwargs:/MinDynamicKwargs
    ...    --port    ${PORT}
    Sleep    3s

Teardown suite
    Run keyword and ignore error    Stop Remote Server
    Terminate Process
    Sleep    3s
    Process Should Be Stopped
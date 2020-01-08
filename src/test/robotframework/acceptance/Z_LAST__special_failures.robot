*** Settings ***
Resource          resource.robot

*** Test Cases ***
Not special
    [Documentation]  FAIL org.robotframework.examplelib.impl.SpecialException: message
    Run keyword and expect error    org.robotframework.examplelib.impl.SpecialException: message    Not special    message

Continuable
    [Documentation]  FAIL Several failures occurred:\n\n
    ...    1) org.robotframework.examplelib.impl.SpecialException: message\n\n
    ...    2) org.robotframework.examplelib.impl.SpecialException: second message\n\n
    ...    3) org.robotframework.examplelib.impl.SpecialException: third message
    Run keyword and expect error    org.robotframework.examplelib.impl.SpecialException: message    Continuable    message
    Run keyword and expect error    org.robotframework.examplelib.impl.SpecialException: second message    Continuable    second message
    Run keyword and expect error    org.robotframework.examplelib.impl.SpecialException: third message    Continuable    third message

Fatal
    [Tags]    fatal
    [Documentation]  FAIL org.robotframework.examplelib.impl.SpecialException: Execution ends here
    Run keyword and expect error    org.robotframework.examplelib.impl.SpecialException: Execution ends here    Fatal    Execution ends here
    Fail    This should not be executed

Fails due to earlier fatal error
    [Tags]    fatal
    [Documentation]  FAIL Test execution stopped due to a fatal error.
    Fail    This should not be executed
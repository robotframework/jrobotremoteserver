*** Settings ***
Resource          resource.robot

*** Test Cases ***
Base Exception
    [Documentation]    FAIL My message
    Run Keyword And Expect Error    My message    Base Exception

Exception Without Message
    [Documentation]    FAIL Exception
    Run Keyword And Expect Error    Exception    Exception Without Message

AssertionError
    [Documentation]    FAIL Failure message
    Run Keyword And Expect Error    Failure message    Assertion Error

RuntimeError
    [Documentation]    FAIL Error message
    Run Keyword And Expect Error    Error message    Runtime Error

Zero Division
    [Documentation]    FAIL java.lang.ArithmeticException: / by zero
    Run Keyword And Expect Error    java.lang.ArithmeticException: / by zero    Zero Division

Custom Exception
    [Documentation]    FAIL org.robotframework.examplelib.impl.MyException: My message
    Run Keyword And Expect Error    org.robotframework.examplelib.impl.MyException: My message    Custom Exception

Suppressed Name Exception
    Run Keyword And Expect Error    name suppressed    Suppressed Name Exception    ${True}
    Run Keyword And Expect Error    name suppressed    Suppressed Name Exception    ${False}

Failure Deeper
    [Documentation]    FAIL Finally failing
    [Tags]    todo
    Run Keyword And Expect Error    Finally failing    Failure Deeper

Error Message With Non-ASCII Unicode
    [Documentation]    FAIL ${UNICODE STRING}
    Run Keyword And Expect Error    ${UNICODE STRING}    Error Message With Non ASCII Unicode


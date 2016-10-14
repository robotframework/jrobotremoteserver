*** Settings ***
Documentation     Test suite to verify OSGI capabilities of jrobot-remoe-server
Suite Setup       Setup Suite
Suite Teardown    Clean Suite
Resource          ../karaf_keywords.robot

*** Variables ***

*** Test Cases ***
Library Type Override Test
    [Documentation]    TODO
    ${result}    BaseLib.Concat    Concat me    together.
    Should Be Equal As Strings    ${result}    Concat me together.
    ${result}    BaseLib.Concat    Number    5
    Should Be Equal As Strings    ${result}    Number 6
    ${result}    BaseLib.Concat    5    Number
    Should Be Equal As Strings    ${result}    6 Number
    ${result}    BaseLib.Concat    Concat    me    together.
    Should Be Equal As Strings    ${result}    Concat me together.

Library Numeric Type Override Test
    [Documentation]    TODO
    ${sum}    BaseLib.Add    5.2    1.3
    Should be equal as numbers    6.5    ${sum}
    ${sum}    BaseLib.Add    5    1
    Should Be Equal As Integers    6    ${sum}

Library Inheritance Test
    [Documentation]
    ${name_1}    BaseLib.Get Name
    ${name_2}    ExtendedLib.Get Name
    Should Not Be Equal As Strings    ${name_1}    ${name_2}
    Should Be Equal As Strings    ${name_1}    Base Library
    Should Be Equal As Strings    ${name_2}    Extended Library
    ${pi}    ExtendedLib.Get Pi
    Should be equal as numbers    3.14    ${pi}

*** Keywords ***
Setup Suite
    [Documentation]    TODO
    Start Karaf
    Verify Feature Installed On Karaf    jrobot-remote-server
    Verify Feature Started On Karaf    jrobot-remote-server
    Install Bundle On Karaf    mvn:com.github.aenniw/jrobot-test-library
    Verify Bundle Installed On Karaf    jrobot-test-library
    Start Bundle On Karaf    jrobot-test-library
    Verify Bundle Started On Karaf    jrobot-test-library
    Import Library    Remote     http://localhost:8270/BaseLibrary    WITH NAME    BaseLib
    Import Library    Remote     http://localhost:8270/ExtendedLibrary    WITH NAME    ExtendedLib

Clean Suite
    [Documentation]
    BaseLib.Library Cleanup
    ExtendedLib.Library Cleanup
    Stop Karaf

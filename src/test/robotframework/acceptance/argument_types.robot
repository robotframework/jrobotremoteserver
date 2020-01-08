*** Settings ***
Resource        resource.robot

*** Test Cases ***
String As Argument
    String As Argument    ${BYTE STRING}

Non-ASCII String As Argument
    Unicode String As Argument    ${UNICODE STRING}

Control Char As Argument
    [Documentation]    Fails with ExpatError when server runs on Jython 2.5.
    [Tags]    todo
    Control Char As Argument    ${CONTROL CHAR}

Empty String As Argument
    Empty String As Argument    ${EMPTY}

Integer As Argument
    Integer As Argument    ${42}
    Negative Integer As Argument    ${-1}

Int As Argument Conversion From String
    [Documentation]    JavalibCore does the conversion, this is just a sanity check
    ${number}=    Int As Argument    42
    Should Be Equal    ${number}    ${42}

Double As Argument
    Double As Argument    ${9.34}
    Double Object As Argument    ${4.2}

Zero As Argument
    Zero As Argument    ${0}

Boolean As Argument
    Boolean True As Argument    ${True}
    Boolean False As Argument    ${False}
    Boolean Object As Argument    ${False}

Null As Argument
    [Documentation]    None is converted to empty string because it is not supported by all XML-RPC versions.
    Null As Argument    ${None}

Arbitrary Object As Argument
    [Documentation]    Arbitrary objects cannot be transferred over XML-RPC and thus only their string presentation is used
    Object As Argument    ${MyObject()}

List As Argument
    Log    ${LIST}
    List As Argument    ${LIST}
    Empty List As Argument    ${EMPTY LIST}

List Containing None As Argument
    List Containing None As Argument    ${LIST WITH NONE}

List Containing Arbitrary Objects As Argument
    List Containing Objects As Argument    ${LIST WITH OBJECTS}

Nested List As Argument
    Nested List As Argument    ${NESTED LIST}

Map As Argument
    Map As Argument    ${DICT}
    Empty Map As Argument    ${EMPTY DICT}

Map With Non-String Keys As Argument
    [Documentation]    XML-RPC supports only strings as keys so must convert them
    Map With Non String Keys As Argument    ${DICT WITH NON STRING KEYS}

Map Containing None As Argument
    Map Containing None As Argument    ${DICT WITH NONE}

Map Containing Objects As Argument
    Map Containing Objects As Argument    ${DICT WITH OBJECTS}

Nested Map As Argument
    Nested Map As Argument    ${NESTED DICT}

Array As Argument
    Array As Argument    ${LIST_WITH_INTEGERS}    ${-2}

File As Argument
    ${file}    Set Variable    test_file
    Create Binary File    ${file}    \x01\x00\xe4\x00
    ${file_data}    Get Binary File    ${file}
    ${response}    Binary File As Argument    ${file_data}
    Create Binary File    response_file   ${response}
    ${response_data}    Get Binary File    response_file
    Should Be Equal    ${response_data}    ${file_data}


*** Settings ***
Suite Setup       Set Debug Log Level
Suite Teardown    Reset Log Level
Resource          resource.robot

*** Test Cases ***
One message Without Level
      [Documentation]    LOG 1 INFO Hello, world!
      One Message Without Level

Log Levels
      [Documentation]    LOG 1 DEBUG Debug message\nLOG 2 INFO Information message\nLOG 3 WARN Warning message
      Logging    Debug message    DEBUG
      Logging    Information message    INFO
      Logging    Warning message    WARN

Multiple Messages With Different Levels
      [Documentation]    LOG 1:1 INFO Info message\nLOG 1:2 DEBUG Debug message\nLOG 1:3 INFO Second info\n this time with two lines\nLOG 1:4 INFO Third info\nLOG 1:5 WARN Warning
      Multiple Messages With Different Levels

Log Unicode
      [Documentation]    LOG 1 INFO ${UNICODE STRING}
      Log Unicode

Logging And Failing
      [Documentation]    FAIL Too slow\nLOG 1:1 INFO This keyword will fail!\nLOG 1:2 WARN Run for your lives!!\nLOG 1:3 FAIL Too slow\nLOG 1:4 DEBUG REGEXP: [^:]+: Too slow.*
      Run keyword and expect error    Too slow    Logging And Failing

Logging And Returning
      [Documentation]    LOG 1:1 INFO Logged message\nLOG 1:2 INFO \${ret} = Returned value\nLOG 3:1 WARN This keyword returns nothing\nLOG 3:2 INFO \${ret} = ${EMPTY}
      ${ret} =    Logging And Returning
      Should Be Equal    ${ret}    Returned value
      ${ret} =    Logging    This keyword returns nothing    WARN
      Should Be Equal    ${ret}    ${EMPTY}

Log Control Char
      [Documentation]    Expected unicode char has a \x01 in it.\nLOG 1:1 INFO ģ
      Log Control Char

Logging both through stdout and stderr
     [Documentation]    LOG 1:1 INFO stdout\nLOG 1:2 INFO stderr\nLOG 2:1 DEBUG stdout-continue\nLOG 2:2 INFO stderr\nLOG 3:1 INFO o\no2\nLOG 3:2 DEBUG e\nLOG 3:3 INFO e2
     Logging both to stdout and stderr    stdout    stderr
     Logging both to stdout and stderr    *DEBUG* stdout    stderr    -continue
     Logging both to stdout and stderr    o\n    *DEBUG* e\n    o2    *INFO* e2

*** Settings ***
Suite Setup       Set Debug Log Level
Suite Teardown    Reset Log Level
Resource          resource.robot

*** Test Cases ***
Remote Traceback Is Shown In Log
      [Documentation]    FAIL My error message\nLOG 1:1 FAIL My error message\nLOG 1:2 DEBUG REGEXP: .*\n\\s+at [^\\(]+\\(.*java:\\d+\\).*
      Run keyword and expect error    My error message    Failing    My error message

Remote Traceback With Multiple Entries
      [Documentation]    FAIL Finally failing\nLOG 1:1 FAIL Finally failing\nLOG 1:2 DEBUG REGEXP: [^:]+: Finally failing.*
      [Tags]    todo
      Run keyword and expect error    Finally failing    Failure Deeper

Local Traceback Is Not Shown In Log
      [Documentation]    FAIL Yet another error\nLOG 1:1 FAIL Yet another error\nLOG 1:2 DEBUG REGEXP: .*\n\\s+at [^\\(]+\\(.*java:\\d+\\).*\nLOG 1:3 NONE
      Run keyword and expect error    Yet another error    Failing    Yet another error


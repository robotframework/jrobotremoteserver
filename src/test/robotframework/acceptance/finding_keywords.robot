*** Settings ***
Resource          resource.robot

*** Test Cases ***
Private Methods Should Be Ignored
      [Documentation]    FAIL No keyword with name 'Private Method' found.
      Run keyword and expect error    No keyword with name 'Private Method' found.    Private Method

Static Methods Should Be Supported
      ${breakfast}=    Static Method
      Should Be Equal    ${breakfast}    eggs

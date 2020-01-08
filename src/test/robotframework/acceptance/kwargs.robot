*** Settings ***
Force Tags        kwargs
Resource          resource.robot

*** Test Cases ***
Kwargs Are Handled
    ${value}=    Get Kwarg Value    eggs    eggs=${2}
    Should Be Equal    ${value}    ${2}

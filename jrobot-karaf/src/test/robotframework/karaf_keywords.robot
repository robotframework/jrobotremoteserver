*** Settings ***
Documentation     Resource containing keywords manipulating with apache.karaf
Library           OperatingSystem

*** Variables ***
${KARAF_ROOT}     ./jrobot-karaf/target/assembly

*** Keywords ***
Start Karaf
    [Documentation]    Starts karaf container instance
    ${rc}    Run and Return RC    ${KARAF_ROOT}/bin/start.bat
    ${rc}    Run Keyword If    ${rc} != 0    Run and Return RC    ${KARAF_ROOT}/bin/start
    Should Be Equal As Integers    ${rc}    0
    Wait Until Keyword Succeeds    10    1    Karaf Status    Running
    Wait Until Keyword Succeeds    120    1    Run On Karaf    info

Stop Karaf
    [Documentation]    Stops karaf container instance
    ${rc}    Run and Return RC    ${KARAF_ROOT}/bin/stop.bat
    ${rc}    Run Keyword If    ${rc} != 0    Run and Return RC    ${KARAF_ROOT}/bin/stop
    Should Be Equal As Integers    ${rc}    0
    Wait Until Keyword Succeeds    10    1    Karaf Status    Not Running

Karaf Status
    [Arguments]    ${expected_status}
    [Documentation]    Checks status of karaf container
    ${output}    Run    ${KARAF_ROOT}/bin/status.bat
    ${output}    Run Keyword If    '${expected_status}' not in '${output}'    run    ${KARAF_ROOT}/bin/status
    Should Contain    ${output}    ${expected_status}

Run On Karaf
    [Arguments]    ${cmd}
    [Documentation]    Issue command on karaf console
    ${rc}    ${output}    Run and Return RC and Output    ${KARAF_ROOT}/bin/client.bat "${cmd}"
    ${rc}    ${output}    Run Keyword If    ${rc} != 0    run and return rc and output    ${KARAF_ROOT}/bin/client "${cmd}"
    Should Be Equal As Integers    ${rc}    0
    [Return]    ${output}

Install Feature On Karaf
    [Arguments]    ${feature_name}
    [Documentation]    Install specified feature
    Run On Karaf    feature:install ${feature_name}

Install Bundle On Karaf
    [Arguments]    ${bundle_uri}
    [Documentation]    Install bundle specified with URI
    Run On Karaf    bundle:install ${bundle_uri}

Start Feature On Karaf
    [Arguments]    ${feature_name}
    [Documentation]    Start already installed feature
    Run On Karaf    feature:start ${feature_name}

Start Bundle On Karaf
    [Arguments]    ${bundle_name}
    [Documentation]    Start already installed bundle
    Run On Karaf    bundle:start ${bundle_name}

Verify Feature Installed On Karaf
    [Arguments]    ${feature_name}
    [Documentation]    Checks if feature was installed
    ${output}    Run On Karaf    feature:list -i
    Should Contain    ${output}    ${feature_name}

Verify Bundle Installed On Karaf
    [Arguments]    ${bundle_name}
    [Documentation]    Checks if bundle was installed
    ${output}    Run On Karaf    bundle:list
    Should Contain    ${output}    ${bundle_name}

Verify Feature Started On Karaf
    [Arguments]    ${feature_name}
    [Documentation]    Checks if feature was started
    ${output}    Run On Karaf    feature:list -i | grep ${feature_name}
    Should Contain    ${output}    Started

Verify Bundle Started On Karaf
    [Arguments]    ${bundle_name}
    [Documentation]    Checks if bundle was started
    ${output}    Run On Karaf    bundle:list | grep ${bundle_name}
    Should Contain    ${output}    Active

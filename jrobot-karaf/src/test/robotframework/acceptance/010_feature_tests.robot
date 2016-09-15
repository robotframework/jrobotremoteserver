*** Settings ***
Documentation     Test suite to verify OSGI capabilities of jrobot-remoe-server
Suite Setup       Start Karaf
Suite Teardown    Stop Karaf
Resource          ../karaf_keywords.robot

*** Variables ***

*** Test Cases ***
JRobot Feature Test
    [Documentation]    Tests if jrobot-remote-server feature is deployable
    Verify Feature Installed On Karaf    jrobot-remote-server
    Verify Feature Started On Karaf    jrobot-remote-server

JRobot Library Test
    [Documentation]    Tests if jrobot-remote-server feature accepts bundled libraries
    Install Bundle On Karaf    mvn:com.github.aenniw/jrobot-test-library
    Verify Bundle Installed On Karaf    jrobot-test-library
    Start Bundle On Karaf    jrobot-test-library
    Verify Bundle Started On Karaf    jrobot-test-library

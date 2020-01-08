*** Settings ***
Library          Remote    localhost:${PORT}${PATH}
Library          OperatingSystem
Variables        variables.py

*** Variables ***
${ORIG LOG LEVEL}    INFO
${PORT}              8270
${PATH}              ${LIBRARY}

*** Keywords ***
Set Debug Log Level
    ${ORIG LOG LEVEL} =    Set Log Level    DEBUG
    Set Suite Variable    ${ORIG LOG LEVEL}

Reset Log Level
    Set Log Level    ${ORIG LOG LEVEL}

Import remote library
    Import Library    Remote    localhost:${PORT}${PATH}

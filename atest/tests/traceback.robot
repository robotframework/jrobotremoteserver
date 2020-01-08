| *** Settings *** |
| Suite Setup    | Set Debug Log Level |
| Suite Teardown | Reset Log Level |
| Resource       | resource.robot |

| *** Test Cases *** |
| Remote Traceback Is Shown In Log |
|    | [Documentation] | FAIL My error message\nLOG 1:1 FAIL My error message\nLOG 1:2 DEBUG REGEXP: .*\n\\s+at [^\\(]+\\(.*java:\\d+\\).* |
|    | Failing | My error message |

| Remote Traceback With Multiple Entries |
|    | [Documentation] | FAIL Finally failing\nLOG 1:1 FAIL Finally failing\nLOG 1:2 DEBUG REGEXP: [^:]+: Finally failing.* |
|    | Failure Deeper |

| Local Traceback Is Not Shown In Log |
|    | [Documentation] | FAIL Yet another error\nLOG 1:1 FAIL Yet another error\nLOG 1:2 DEBUG REGEXP: .*\n\\s+at [^\\(]+\\(.*java:\\d+\\).*\nLOG 1:3 NONE |
|    | Failing | Yet another error |


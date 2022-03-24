| *** Settings *** |
| Documentation  | A suite of acceptance tests.\n\nEven though the library could arguably be used locally for acceptance testing, this suite is true end-to-end testing because the library is accessed remotely. |
| Suite Setup    | Run Keywords | Start Remote Server If Building | Import Remote Library |
| Suite Teardown | Stop Remote Server If Building |
| Library        | TypeLibrary.py |
| Resource       | Resources/common.txt |

| *** Variables *** |

| *** Test Cases *** |
| Add And Remove Elements To Queue |
|    | Clear Queue |
|    | Add To Queue | ${42} |
|    | Add To Queue | ${3.14} |
|    | Add To Queue | Hail to the robots |
|    | Log Queue |
|    | ${queue} | Get Queue |
|    | Length Should Be | ${queue} | 3 |
|    | ${value} | Remove From Queue |
|    | Should Be Int | ${value} |
|    | Should Be Equal | ${value} | ${queue[0]} |
|    | ${value} | Remove From Queue |
|    | Should Be Float | ${value} |
|    | Should Be Equal | ${value} | ${queue[1]} |
|    | ${value} | Remove From Queue |
|    | Should Be String | ${value} |
|    | Should Be Equal | ${value} | ${queue[2]} |
|    | ${queue} | Get Queue |
|    | Should Be Empty | ${queue} |

| *** Keywords *** |

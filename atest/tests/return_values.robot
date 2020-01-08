| *** Settings *** |
| Test Template  | Return Value Should Be |
| Resource       | resource.robot |

| *** Test Cases *** |
| Return String |
|    | Return String | Hello, world! |

| Return Empty String |
|    | Return Empty String | ${EMPTY} |

| Return Unicode String |
|    | Return Unicode String | ${UNICODE STRING} |

| Return Control Char |
|    | [Documentation] | Fails with ExpatError when running tests on Jython 2.5. |
|    | Return Control Char | ${CONTROL CHAR} |

| Return Short |
|    | Return Short | ${-88} |
|    | Return Short Object | ${9} |

| Return Integer |
|    | Return Int | ${12} |
|    | Return Integer | ${42} |
|    | Return Negative Integer | ${-1} |

| Return Zero |
|    | Return Zero | ${0} |

| Return Float |
|    | Return Float | ${3.14} |
|    | Return Negative Float | ${-0.5} |
|    | Return Float Object | ${86.02} |

| Return Double |
|    | Return Double | ${9.34} |
|    | Return Double Object | ${4.2} |

| Return Byte |
|    | Return Byte | ${67} |
|    | Return Byte Object | ${127} |

| Return Boolean |
|    | Return Boolean True | ${True} |
|    | Return Boolean False | ${False} |
|    | Return Boolean Object | ${False} |

| Return Null And Void |
|    | [Documentation] | None is not supported by all XML-RPC versions and thus it is converted to an empty string |
|    | Return Nothing | ${EMPTY} |
|    | Return Null | ${EMPTY} |

| Return Arbitrary Object |
|    | [Documentation] | Arbitrary objects cannot be transferred over XML-RPC and thus only their string presentation is returned |
|    | Return Object | <MyObject> |

| Return List |
|    | Return List | ${LIST} |
|    | Return Empty List | ${EMPTY LIST} |

| Return List Containing Null |
|    | Return List Containing Null | [''] | eval |

| Return List Containing Arbitrary Objects |
|    | Return List Containing Objects | ['<MyObject1>', '<MyObject2>'] | eval |

| Return Nested List |
|    | Return Nested List | [[True, False], [[1, '', '<MyObject>', {}]]] | eval |

| Return Map |
|    | Return Map | ${DICT} |
|    | Return Empty Map | ${EMPTY DICT} |

| Return Map With Non-String Keys |
|    | [Documentation] | XML-RPC supports only strings as keys so must convert them |
|    | Return Map \ With Non String Keys | {'1': 2, '': 1} | eval |

| Return Map Containing Null |
|    | Return Map Containing Null | {'As value': '', '': 'As key'} | eval |

| Return Map Containing Objects |
|    | Return Map \ Containing Objects | {'As value': '<MyObject1>', '<MyObject2>': 'As key'} | eval |

| Return Nested Map |
|    | Return Nested Map | {'1': {'': False}, '2': {'A': {'n': ''}, 'B': {'o': '<MyObject>', 'e': {}}}} | eval |

| Return Array Containing Bytes |
|    | Return Array Of Byte | [${-57}, ${21}] | eval |
|    | Return Primitive Array Of Byte | [${-4}, ${8}] | eval |

| Return Array Containing Strings |
|    | Return Array Containing Strings | ['spam', 'eggs'] | eval |

| Return Array Containing Integers |
|    | Return Array Of Short | [${34}, ${-98}] | eval |
|    | Return Primitive Array Of Short | [${287}, ${-86}] | eval |
|    | Return Array Of Integer | [${3}, ${-5}] | eval |
|    | Return Primitive Array Of Int | [${-338}, ${897}] | eval |
|    | Return Array Of Long | ['-9223372036854775800', '9223372036854775805'] | eval | # strings as longs do not fit in I4 |
|    | Return Primitive Array Of Long | ['8723372036854774829', '-9123372036854775382'] | eval |

| Return Array Containing Rational Numbers |
|    | Return Array Of Float | [${8.2}, ${-7.38}] | eval |
|    | Return Primitive Array Of Float | [${-7.774}, ${562.1}] | eval |
|    | Return Array Of Double | [${101.6}, ${45.67}] | eval |
|    | Return Primitive Array Of Double | [${-37.11}, ${90.4}] | eval |
|    | Return Primitive Array Of Boolean | [${True}, ${False}] | eval |

| Return Array Containing Booleans |
|    | Return Array Of Boolean | [${False}, ${True}] | eval |
|    | Return Primitive Array Of Boolean | [${True}, ${False}] | eval |

| Return Array Containing Chars |
|    | Return Array Containing Chars | OK |

| Return Array Containing Arbitrary Objects |
|    | Return Array Of Objects | ['<MyObject1>', '<MyObject2>'] | eval |

| Return Iterable |
|    | Return Queue | ['first', '<MyObjectsecond>'] | eval |
|    | Return Tree Set | [${2}, ${5}] | eval |
|    | Return Empty Queue | [] | eval |

| Return Date |
|    | Return Date | Wed Dec 31 18:00:00 CST 1969 |

| *** Keywords *** |
| Return Value Should Be |
|    | [Arguments] | ${keyword} | ${expected} | ${evaluate}= |
|    | ${actual} = | Run Keyword | ${keyword} |
|    | ${tester} = | Set Variable If | "${evaluate}" | Should Be Equal Evaluated | Should Be Equal |
|    | Run Keyword | ${tester} | ${actual} | ${expected} |

| Should Be Equal Evaluated |
|    | [Arguments] | ${actual} | ${expected} |
|    | ${expected} = | Evaluate | ${expected} |
|    | Should Be Equal | ${actual} | ${expected} |


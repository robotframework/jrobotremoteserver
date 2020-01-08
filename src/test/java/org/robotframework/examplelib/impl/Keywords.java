package org.robotframework.examplelib.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;

import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

/*
 * Various type declarations are used on purpose to provide more coverage
 */
@RobotKeywords
public class Keywords {

    private final String unicode = "Hyv\u00E4\u00E4 y\u00F6t\u00E4. " + "\u0421\u043F\u0430\u0441\u0438\u0431\u043E!";

    // Common keywords

    @RobotKeyword("Documentation")
    public void hasDocumentation() {
    }

    @ArgumentNames({ "name" })
    public void hasArgumentDocumentation(String name) {
    }

    @RobotKeyword
    public String getServerLanguage() {
        return "Java";
    }

    @RobotKeyword("This keyword passes.\n" + "\n"
            + "See `Failing`, `Logging`, and `Returning` for other basic keywords.")
    public void passing() {
    }

    @RobotKeyword("This keyword fails with provided `message`")
    @ArgumentNames({ "message" })
    public static void failing(String message) {
        throw new AssertionError(message);
    }

    @RobotKeyword("This keywords logs given `message` with given `level`\n" + "\n" + "Example:"
            + "| Logging | Hello, world! |      |" + "| Logging | Warning!!!    | WARN |")
    @ArgumentNames({ "message", "level=INFO" })
    public void logging(String message, String level) {
        System.out.println(String.format("*%s* %s", level, message));
    }

    @RobotKeyword("This keyword returns a string 'returned string'.")
    public String returning() {
        return "returned string";
    }

    // Logging

    @RobotKeyword
    public void oneMessageWithoutLevel() {
        System.out.println("Hello, world!");
    }

    @RobotKeyword
    public void multipleMessagesWithDifferentLevels() {
        System.out.println("Info message");
        System.out.println("*DEBUG* Debug message");
        System.out.println("*INFO* Second info");
        System.out.println("this time with two lines");
        System.out.println("*INFO* Third info");
        System.out.println("*TRACE* This is ignored");
        System.out.println("*WARN* Warning");
    }

    @RobotKeyword
    public void logUnicode() {
        System.out.println(unicode);
    }

    @RobotKeyword
    public static void loggingAndFailing() {
        System.out.println("*INFO* This keyword will fail!");
        System.out.println("*WARN* Run for your lives!!");
        throw new RuntimeException("Too slow");
    }

    @RobotKeyword
    public String loggingAndReturning() {
        System.out.println("Logged message");
        return "Returned value";
    }

    @RobotKeyword
    public void logControlChar() {
        System.out.println("\u0123");
    }

    @RobotKeyword
    @ArgumentNames({ "*varargs" })
    public void loggingBothToStdoutAndStderr(String... messages) {
        for (int i = 0; i < messages.length; i++) {
            if (i % 2 == 0) {
                System.out.print(messages[i]);
            } else {
                System.err.print(messages[i]);
            }
        }
    }

    // Failures

    @RobotKeyword
    public static void baseException() throws Exception {
        throw new Exception("My message");
    }

    @RobotKeyword
    public static void exceptionWithoutMessage() throws Exception {
        throw new Exception();
    }

    @RobotKeyword
    public static void assertionError() {
        throw new AssertionError("Failure message");
    }

    @RobotKeyword
    public void runtimeError() {
        throw new RuntimeException("Error message");
    }

    @RobotKeyword
    public static void indexError() {
        System.out.println(new String[] {}[0]);
    }

    @RobotKeyword
    public static void zeroDivision() {
        System.out.println(1 / 0);
    }

    @RobotKeyword
    public static void customException() throws MyException {
        throw new MyException("My message");
    }

    @RobotKeyword
    @ArgumentNames({ "subClass" })
    public static void suppressedNameException(boolean subClass) throws SuppressedNameException {
        if (subClass) {
            throw new SpecificSuppressedNameException();
        }
        throw new SuppressedNameException();
    }

    @RobotKeyword
    @ArgumentNames({ "rounds=10" })
    public static void failureDeeper(Integer rounds) {
        if (rounds == 1)
            throw new RuntimeException("Finally failing");
        failureDeeper(rounds - 1);
    }

    @RobotKeyword
    public void errorMessageWithNonAsciiUnicode() {
        throw new RuntimeException(unicode);
    }

    @RobotKeyword
    @ArgumentNames({ "message" })
    static public void notSpecial(String message) {
        throw new SpecialException(message,  false, false);
    }

    @RobotKeyword
    @ArgumentNames({ "message" })
    static public void continuable(String message) {
        throw new SpecialException(message, true, false);
    }

    @RobotKeyword
    @ArgumentNames({ "message" })
    static public void fatal(String message) {
        throw new SpecialException(message, false, true);
    }

    // Arguments counts

    @RobotKeyword
    public String noArguments() {
        return "no arguments";
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public Object oneArgument(Object arg) {
        System.out.print(String.format("%s", arg)); //
        return arg;
    }

    @RobotKeyword
    @ArgumentNames({ "arg1", "arg2" })
    public Object twoArguments(Object arg1, Object arg2) {
        System.out.print(String.format("%s %s", arg1, arg2)); //
        return String.format("%s %s", arg1, arg2);
    }

    @RobotKeyword
    @ArgumentNames({ "arg1", "arg2", "arg3", "arg4", "arg5", "arg6", "arg7" })
    public Object sevenArguments(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6,
            Object arg7) {
        return String.format("%s %s %s %s %s %s %s", arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    @RobotKeyword
    @ArgumentNames({ "arg1", "arg2=2", "arg3=3" })
    public Object argumentsWithDefaultValues(Object arg1, Object arg2, Object arg3) {
        return String.format("%s %s %s", arg1, arg2, arg3);
    }

    @RobotKeyword
    @ArgumentNames("*args")
    public String variableNumberOfArguments(Object... args) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object arg : args) {
            if (!first)
                sb.append(" ");
            first = false;
            sb.append(arg.toString());
        }
        return sb.toString();
    }

    @RobotKeyword
    @ArgumentNames({ "req", "default=world", "*varargs" })
    public String requiredDefaultsAndVarargs(Object req, String defaultArg, Object... varargs) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %s", req, defaultArg));
        for (int i = 0; i < varargs.length; i++)
            sb.append(" " + varargs[i].toString());
        return sb.toString();
    }

    // Argument types

    @RobotKeyword
    @ArgumentNames("arg")
    public void stringAsArgument(Object arg) {
        shouldBeEqual(returnString(), (String) arg);
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void unicodeStringAsArgument(String arg) {
        shouldBeEqual(arg, unicode);
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void emptyStringAsArgument(Object arg) {
        shouldBeEqual(arg, "");
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void controlCharAsArgument(String arg) {
        shouldBeEqual(arg, "\1");
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void integerAsArgument(Object arg) {
        shouldBeEqual(arg, returnInteger());
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public int intAsArgument(int arg) {
        return arg;
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void negativeIntegerAsArgument(Integer arg) {
        shouldBeEqual(arg, returnNegativeInteger());
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void doubleAsArgument(double arg) {
        shouldBeEqual(arg, returnDouble());
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void doubleObjectAsArgument(Double arg) {
        shouldBeEqual(arg, returnDoubleObject());
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void zeroAsArgument(Object arg) {
        shouldBeEqual(arg, 0);
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void booleanTrueAsArgument(Object arg) {
        shouldBeEqual(arg, true);
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void booleanFalseAsArgument(boolean arg) {
        shouldBeEqual(arg, false);
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void booleanObjectAsArgument(Boolean arg) {
        shouldBeEqual(arg, false);
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void nullAsArgument(Object arg) {
        shouldBeEqual(arg, "");
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void objectAsArgument(Object arg) {
        shouldBeEqual(arg, "<MyObject>");
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void listAsArgument(List<?> arg) {
        shouldBeEqual(arg, returnList());
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void emptyListAsArgument(List<?> arg) {
        shouldBeEqual(arg, new ArrayList<Object>());
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void listContainingNoneAsArgument(List<Object> arg) {
        shouldBeEqual(arg, Arrays.asList(""));
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void listContainingObjectsAsArgument(List<Object> arg) {
        shouldBeEqual(arg, Arrays.asList("<MyObject1>", "<MyObject2>"));
    }

    @RobotKeyword
    @ArgumentNames("arg")
    public void nestedListAsArgument(List<Object> arg) {
        System.out.println(arg.getClass().getName() + arg);
        List<Object> exp = new ArrayList<Object>();
        exp.add(Arrays.asList(true, false));
        List inner = new ArrayList();
        inner.add(Arrays.asList(1, "", "<MyObject>", new HashMap()));
        exp.add(inner);
        // exp.add(4);
        shouldBeEqual(arg, exp);
    }

    @RobotKeyword
    @ArgumentNames({ "arg" })
    public void mapAsArgument(Map<Object, Object> arg) {
        shouldBeEqual(arg, returnMap());
    }

    @RobotKeyword
    @ArgumentNames({ "arg" })
    public void emptyMapAsArgument(Map<?, ?> arg) {
        shouldBeEqual(arg, new HashMap());
    }

    @RobotKeyword
    @ArgumentNames({ "arg" })
    public void mapWithNonStringKeysAsArgument(Map<Object, Object> arg) {
        Map exp = new HashMap();
        exp.put("1", 2);
        exp.put("", true);
        shouldBeEqual(arg, exp);
    }

    @RobotKeyword
    @ArgumentNames({ "arg" })
    public void mapContainingNoneAsArgument(Map<?, ?> arg) {
        Map exp = new HashMap();
        exp.put("As value", "");
        exp.put("", "As key");
        shouldBeEqual(arg, exp);
    }

    @RobotKeyword
    @ArgumentNames({ "arg" })
    public void mapContainingObjectsAsArgument(Map<?, ?> arg) {
        Map exp = new HashMap();
        exp.put("As value", "<MyObject1>");
        exp.put("<MyObject2>", "As key");
        shouldBeEqual(arg, exp);
    }

    @RobotKeyword
    @ArgumentNames({ "arg" })
    public void nestedMapAsArgument(Object arg) {
        Map exp = new HashMap();
        Map m1 = new HashMap();
        m1.put("", false);
        exp.put("1", m1);
        Map m2 = new HashMap();
        m2.put("n", "");
        m1 = new HashMap();
        m1.put("A", m2);
        m2 = new HashMap();
        m2.put("o", "<MyObject>");
        m2.put("e", new HashMap());
        m1.put("B", m2);
        exp.put("2", m1);
        shouldBeEqual(arg, exp);
    }

    @RobotKeyword
    @ArgumentNames({ "nums", "sum" })
    public void arrayAsArgument(Object[] nums, Integer sum) {
        Integer asum = 0;
        for (Object num : nums)
            asum += (Integer) num;
        shouldBeEqual(sum, asum);
    }

    @RobotKeyword
    @ArgumentNames({ "file" })
    public byte[] binaryFileAsArgument(byte[] file) {
        return file;
    }

    // Return values

    @RobotKeyword
    public String returnString() {
        return "Hello, world!";
    }

    @RobotKeyword
    public String returnUnicodeString() {
        return unicode;
    }

    @RobotKeyword
    public String returnEmptyString() {
        return "";
    }

    @RobotKeyword
    public int returnInt() {
        return 12;
    }

    @RobotKeyword
    public Integer returnInteger() {
        return 42;
    }

    @RobotKeyword
    public Integer returnNegativeInteger() {
        return -1;
    }

    @RobotKeyword
    public float returnFloat() {
        return 3.14f;
    }

    @RobotKeyword
    public float returnNegativeFloat() {
        return -0.5f;
    }

    @RobotKeyword
    public Float returnFloatObject() {
        return 86.02f;
    }

    @RobotKeyword
    public int returnZero() {
        return 0;
    }

    @RobotKeyword
    public boolean returnBooleanTrue() {
        return true;
    }

    @RobotKeyword
    public boolean returnBooleanFalse() {
        return false;
    }

    @RobotKeyword
    public Boolean returnBooleanObject() {
        return false;
    }

    @RobotKeyword
    public void returnNothing() {
    }

    @RobotKeyword
    public Object returnNull() {
        return null;
    }

    @RobotKeyword
    public Object returnObject() {
        return new MyObject("");
    }

    @RobotKeyword
    public List<? extends Object> returnList() {
        return Arrays.asList("One", -2, false);
    }

    @RobotKeyword
    public List<Object> returnEmptyList() {
        return new ArrayList<Object>();
    }

    @RobotKeyword
    public List<Object> returnListContainingNull() {
        return Arrays.asList(new Object[] { null });
    }

    @RobotKeyword
    public List returnListContainingObjects() {
        return Arrays.asList(new MyObject("1"), new MyObject("2"));
    }

    @RobotKeyword
    public List<Object> returnNestedList() {
        List<Object> outer = new ArrayList<Object>();
        List<Object> inner = Arrays.asList(new Object[] { true, false });
        outer.add(inner);
        List<Object> inner2 = new ArrayList<Object>(Arrays.asList(new Object[] { 1, null, new MyObject(""),
                new HashMap<Object, Object>() }));
        inner = new ArrayList<Object>();
        inner.add(inner2);
        outer.add(inner);
        return outer;
    }

    @RobotKeyword
    public Map<Object, Object> returnMap() {
        Map<Object, Object> dic = new HashMap<Object, Object>();
        dic.put("one", 1);
        dic.put("spam", "eggs");
        return dic;
    }

    @RobotKeyword
    public Map<Object, Object> returnEmptyMap() {
        return new HashMap<Object, Object>();
    }

    @RobotKeyword
    public Map<Object, Object> returnMapWithNonStringKeys() {
        Map<Object, Object> dic = new HashMap<Object, Object>();
        dic.put(1, 2);
        dic.put(null, true);
        return dic;
    }

    @RobotKeyword
    public Map<Object, Object> returnMapContainingNull() {
        Map<Object, Object> dic = new HashMap<Object, Object>();
        dic.put("As value", null);
        dic.put(null, "As key");
        return dic;
    }

    @RobotKeyword
    public Map<Object, Object> returnMapContainingObjects() {
        Map<Object, Object> dic = new HashMap<Object, Object>();
        dic.put("As value", new MyObject("1"));
        dic.put(new MyObject("2"), "As key");
        return dic;
    }

    @RobotKeyword
    public Map<Object, Object> returnNestedMap() {
        Map<Object, Object> ret = new HashMap<Object, Object>();
        Map<Object, Object> inner = new HashMap<Object, Object>();
        inner.put(null, false);
        ret.put(1, inner);
        inner = new HashMap<Object, Object>();
        Map<Object, Object> inner2 = new HashMap<Object, Object>();
        inner2.put("n", null);
        inner.put("A", inner2);
        inner2 = new HashMap<Object, Object>();
        inner2.put("o", new MyObject(""));
        inner2.put("e", new HashMap<Object, Object>());
        inner.put("B", inner2);
        ret.put(2, inner);
        return ret;
    }

    @RobotKeyword
    public String returnControlChar() {
        return "\1";
    }

    @RobotKeyword
    public char returnChar() {
        return 'R';
    }

    @RobotKeyword
    public Object[] returnArrayContainingStrings() {
        return new Object[] { "spam", "eggs" };
    }

    @RobotKeyword
    public Double returnDouble() {
        return 9.34;
    }

    @RobotKeyword
    public Double returnDoubleObject() {
        return 4.2;
    }

    @RobotKeyword
    public short returnShort() {
        return -88;
    }

    @RobotKeyword
    public Short returnShortObject() {
        return 9;
    }

    @RobotKeyword
    public byte returnByte() {
        return 67;
    }

    @RobotKeyword
    public Byte returnByteObject() {
        return 127;
    }

    @RobotKeyword
    public char[] returnArrayContainingChars() {
        return new char[] { 79, 75 };
    }

    @RobotKeyword
    public MyObject[] returnArrayOfObjects() {
        return new MyObject[] { new MyObject("1"), new MyObject("2") };
    }

    @RobotKeyword
    public Byte[] returnArrayOfByte() {
        return new Byte[] { (byte) -57, (byte) 21 };
    }

    @RobotKeyword
    public byte[] returnPrimitiveArrayOfByte() {
        return new byte[] { (byte) -4, (byte) 8 };
    }

    @RobotKeyword
    public Short[] returnArrayOfShort() {
        return new Short[] { 34, -98 };
    }

    @RobotKeyword
    public short[] returnPrimitiveArrayOfShort() {
        return new short[] { 287, -86 };
    }

    @RobotKeyword
    public Object[] returnArrayOfInteger() {
        return new Integer[] { 3, -5 };
    }

    @RobotKeyword
    public int[] returnPrimitiveArrayOfInt() {
        return new int[] { -338, 897 };
    }

    @RobotKeyword
    public Long[] returnArrayOfLong() {
        return new Long[] { -9223372036854775800L, 9223372036854775805L };
    }

    @RobotKeyword
    public long[] returnPrimitiveArrayOfLong() {
        return new long[] { 8723372036854774829L, -9123372036854775382L };
    }

    @RobotKeyword
    public Float[] returnArrayOfFloat() {
        return new Float[] { 8.2f, -7.38f };
    }

    @RobotKeyword
    public float[] returnPrimitiveArrayOfFloat() {
        return new float[] { -7.774f, 562.1f };
    }

    @RobotKeyword
    public Double[] returnArrayOfDouble() {
        return new Double[] { 101.6, 45.67 };
    }

    @RobotKeyword
    public double[] returnPrimitiveArrayOfDouble() {
        return new double[] { -37.11, 90.4 };
    }

    @RobotKeyword
    public Boolean[] returnArrayOfBoolean() {
        return new Boolean[] { false, true };
    }

    @RobotKeyword
    public boolean[] returnPrimitiveArrayOfBoolean() {
        return new boolean[] { true, false };
    }

    @RobotKeyword
    public ZonedDateTime returnDate() {
        ZoneId cst = ZoneId.of("America/Chicago");
        ZonedDateTime.ofInstant((new Date(0)).toInstant(), ZoneId.systemDefault());
        ZonedDateTime cstTime = ZonedDateTime.ofInstant((new Date(0)).toInstant(), ZoneId.systemDefault()).withZoneSameInstant(cst);

        return cstTime;
    }

    @RobotKeyword
    public Queue<Object> returnQueue() {
        Queue<Object> queue = new LinkedBlockingQueue<Object>();
        queue.add("first");
        queue.add(new MyObject("second"));
        return queue;
    }

    @RobotKeyword
    public Queue<String> returnEmptyQueue() {
        return new PriorityQueue();
    }

    @RobotKeyword
    public Object returnTreeSet() {
        SortedSet<Integer> s = new TreeSet<Integer>();
        s.add(5);
        s.add(2);
        return s;
    }

    @RobotKeyword
    public static String staticMethod() {
        return "eggs";
    }

    private void privateMethod() {
    }

    private void shouldBeEqual(Object a, Object b) {
        shouldBeEqual(a, b, "");
    }

    /*
     * The equals method of any class is too precise in most classes to use
     * directly, especially on PyObject's subclasses. This method considers
     * equality to mean all Lists and array have the same elements in the same
     * order. For maps, order is ignored and keys are assumed to be strings.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void shouldBeEqual(Object a, Object b, String where) {
        if (a == null && b == null)
            return;
        else if (a == null || b == null)
            throw new AssertionError(String.format("%sone is null other is not %s %s", where, a, b));
        else if (a.equals(b) || b.equals(a))
            return;
        else if (a instanceof List || b instanceof List) {
            if (!(a instanceof List && b instanceof List))
                throw new AssertionError(String.format("%sOne is List other is not: %s %s", where, a.getClass(),
                        b.getClass()));
            List la = new ArrayList((List) a);
            List lb = new ArrayList((List) b);
            if (la.size() != lb.size())
                throw new AssertionError(String.format("%ssize of a: %s != size of b: %s", where, la.size(), lb.size()));
            for (int i = 0; i < la.size(); i++)
                shouldBeEqual(la.get(i), lb.get(i), String.format("%slist(%d)>", where, i));
        } else if (a instanceof Map || b instanceof Map) {
            if (!(a instanceof Map && b instanceof Map))
                throw new AssertionError(String.format("%sOne is Map other is not: %s %s", where, a.getClass(),
                        b.getClass()));
            Map ma = (Map) a;
            Map mb = (Map) b;
            int sa = ma.entrySet().size();
            int sb = mb.entrySet().size();
            if (sa != sb)
                throw new AssertionError(String.format("%ssize of a: %s != size of b: %s", where, sa, sb));
            Set<Entry> seta = ma.entrySet();
            Set<Entry> setb = mb.entrySet();
            for (Entry entry : seta) {
                String key = (String) entry.getKey();
                if (!mb.keySet().contains(key))
                    throw new AssertionError(String.format("%sb missing key %s", where, key));
                shouldBeEqual(entry.getValue(), mb.get(key), String.format("%skey(%s) of a>", where, key));
            }
            for (Entry entry : setb) {
                String key = (String) entry.getKey();
                if (!ma.keySet().contains(key))
                    throw new AssertionError(String.format("%sa missing key %s", where, key));
                shouldBeEqual(entry.getValue(), ma.get(key), String.format("%skey(%s) of a>", where, key));
            }
        } else if (a instanceof Object[] || b instanceof Object[]) {
            if (!(a instanceof Object[] && b instanceof Object[]))
                throw new AssertionError(String.format("%sOne is an array and the other is not: %s %s", where,
                        a.getClass(), b.getClass()));
            Object[] aa = (Object[]) a;
            Object[] ab = (Object[]) b;
            if (aa.length != ab.length)
                throw new AssertionError(String.format("%ssize of a: %s != size of b: %s", where, aa.length, ab.length));
            for (int i = 0; i < aa.length; i++)
                shouldBeEqual(aa, ab, String.format("%sarray(%d)>", where, i));
        } else
            throw new AssertionError(String.format("%sNot equal: %s:%s %s:%s", where, a.getClass(), a, b.getClass(), b));
    }
}

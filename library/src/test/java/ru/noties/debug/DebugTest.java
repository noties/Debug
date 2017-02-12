package ru.noties.debug;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DebugTest {

    // todo, think of how to test trace method calls..

    private static final Level[] LEVELS = Level.values();

    @Rule
    public TestName mTestName = new TestName();

    @Before
    public void before() {
        // clear previous (possible) initialization
        Debug.init();
    }

    @Test
    public void notInitialized() {
        assertFalse(Debug.isDebug());
    }

    @Test
    public void initializedWithVarArgsZero() {
        Debug.init();
        assertFalse(Debug.isDebug());
    }

    @Test
    public void initializedWithCollectionZero() {
        //noinspection unchecked
        Debug.init(Collections.EMPTY_LIST);
        assertFalse(Debug.isDebug());
    }

    @Test
    public void initializedCorrectlyVarArgs() {
        final DebugOutputMock first = new DebugOutputMock(true);
        final DebugOutputMock second = new DebugOutputMock(true);
        final DebugOutputMock third = new DebugOutputMock(true);
        Debug.init(first, second, third);
        assertTrue(Debug.isDebug());
        Debug.v();
        assertEquals(1, first.logItems.size());
        assertEquals(1, second.logItems.size());
        assertEquals(1, third.logItems.size());
    }

    @Test
    public void initializedCorrectlyCollection() {
        final DebugOutputMock first = new DebugOutputMock(true);
        final DebugOutputMock second = new DebugOutputMock(true);
        final DebugOutputMock third = new DebugOutputMock(true);
        Debug.init(Arrays.asList(first, second, third));
        assertTrue(Debug.isDebug());
        Debug.v();
        assertEquals(1, first.logItems.size());
        assertEquals(1, second.logItems.size());
        assertEquals(1, third.logItems.size());
    }

    @Test
    public void mixedDebugFlagsVarArgs() {

        // null is OK
        // if there is at least one non null output that has isDebug = true, than container should return true
        // then only outputs that have isDebug=true must be called with logging

        final DebugOutputMock first = new DebugOutputMock(false);
        final DebugOutputMock second = new DebugOutputMock(true);
        final DebugOutputMock third = null;

        Debug.init(first, second, third);
        assertTrue(Debug.isDebug());

        for (Level level: LEVELS) {
            assertCall(second, level, "second one, yeah", array("second one, yeah"));
        }

        assertEquals(0, first.size());
        assertEquals(LEVELS.length, second.size());
    }

    @Test
    public void mixedDebugFlagsCollection() {

        final DebugOutputMock first = new DebugOutputMock(false);
        final DebugOutputMock second = new DebugOutputMock(true);
        final DebugOutputMock third = null;

        Debug.init(Arrays.asList(first, second, third));
        assertTrue(Debug.isDebug());

        for (Level level: LEVELS) {
            assertCall(second, level, "second one, yeah", array("second one, yeah"));
        }

        assertEquals(0, first.size());
        assertEquals(LEVELS.length, second.size());
    }

    @Test
    public void notDebugDoesNothing() {
        final DebugOutputMock mock = new DebugOutputMock(false);
        Debug.init(mock);
        Debug.wtf("this is a message");
        assertFalse(Debug.isDebug());
        assertEquals(0, mock.size());
    }

    @Test
    public void allLevelsCalledEmpty() {

        final DebugOutputMock mock = new DebugOutputMock(true);
        Debug.init(mock);
        assertTrue(Debug.isDebug());

        for (Level level: LEVELS) {
            assertCall(mock, level, null, null);
        }

        assertEquals(LEVELS.length, mock.size());
    }

    @Test
    public void allLevelsCalledEmptyWithException() {

        final DebugOutputMock mock = new DebugOutputMock(true);
        Debug.init(mock);

        for (Level level: LEVELS) {
            assertCall(mock, level, new NullPointerException(), null, null);
        }
        assertEquals(LEVELS.length, mock.size());
    }

    @Test
    public void allLevelsCalledWithSimpleMessage() {

        final DebugOutputMock mock = new DebugOutputMock(true);
        Debug.init(mock);

        for (Level level: LEVELS) {
            assertCall(mock, level, "Simple one", array("Simple one"));
        }

        assertEquals(LEVELS.length, mock.size());
    }

    @Test
    public void allLevelsCalledWithSimpleMessageAndException() {

        final DebugOutputMock mock = new DebugOutputMock(true);
        Debug.init(mock);

        for (Level level: LEVELS) {
            assertCall(mock, level, new RuntimeException(), "Exception here", array("Exception here"));
        }

        assertEquals(LEVELS.length, mock.size());
    }

    @Test
    public void allLevelsCalledWithEnumeration() {

        final DebugOutputMock mock = new DebugOutputMock(true);
        Debug.init(mock);

        for (Level level: LEVELS) {
            assertCall(mock, level, "0, 1, 2, 3, 4, 5", array(0, 1, 2, 3, 4, 5));
        }

        assertEquals(LEVELS.length, mock.size());
    }

    @Test
    public void allLevelsCalledWithEnumerationAndException() {

        final DebugOutputMock mock = new DebugOutputMock(true);
        Debug.init(mock);

        for (Level level: LEVELS) {
            assertCall(mock, level, new IllegalArgumentException(), "true, hello, 76", array(true, "hello", 76));
        }

        assertEquals(LEVELS.length, mock.size());
    }

    @Test
    public void allLevelsCalledWithFormat() {

        final DebugOutputMock mock = new DebugOutputMock(true);
        Debug.init(mock);

        for (Level level: LEVELS) {
            assertCall(mock, level, "null __ null ++ null", array("%s __ %s ++ %s", null, null, null));
        }

        assertEquals(LEVELS.length, mock.size());
    }

    @Test
    public void allLevelsCalledWithFormatAndException() {

        final DebugOutputMock mock = new DebugOutputMock(true);
        Debug.init(mock);

        for (Level level: LEVELS) {
            assertCall(mock, level, new Exception(), "2 + 2 = 5", array("%1$d + %1$d = %2$s", 2, "5"));
        }

        assertEquals(LEVELS.length, mock.size());
    }

    @Test
    public void tagContains() {

        final DebugOutputMock mock = new DebugOutputMock(true);
        Debug.init(mock);

        Debug.v();
        Debug.d();
        Debug.i();
        Debug.w();
        Debug.e();
        Debug.wtf();

        assertEquals(LEVELS.length, mock.size());

        final String name = mTestName.getMethodName();

        for (LogItem item: mock.logItems) {
            assertTrue(item.tag.contains(name + "(DebugTest.java:"));
        }
    }

    private void assertCall(
            DebugOutputMock mock,
            Level level,
            Throwable throwable,
            String expectedMessage,
            Object[] callingArgs
    ) {
        switch (level) {
            case WTF:
                Debug.wtf(throwable, callingArgs);
                break;
            case E:
                Debug.e(throwable, callingArgs);
                break;
            case W:
                Debug.w(throwable, callingArgs);
                break;
            case I:
                Debug.i(throwable, callingArgs);
                break;
            case D:
                Debug.d(throwable, callingArgs);
                break;
            case V:
                Debug.v(throwable, callingArgs);
                break;
        }

        final LogItem item = mock.last();
        assertEquals(level, item.level);
        assertEquals(throwable, item.throwable);
        assertEquals(expectedMessage, item.message);

        // tags must be tested also independently
        assertTrue(item.tag.contains("assertCall(DebugTest.java:"));
    }

    private void assertCall(
            DebugOutputMock mock,
            Level level,
            String expectedMessage,
            Object[] callingArgs
    ) {
        switch (level) {
            case WTF:
                Debug.wtf(callingArgs);
                break;
            case E:
                Debug.e(callingArgs);
                break;
            case W:
                Debug.w(callingArgs);
                break;
            case I:
                Debug.i(callingArgs);
                break;
            case D:
                Debug.d(callingArgs);
                break;
            case V:
                Debug.v(callingArgs);
                break;
        }

        final LogItem item = mock.last();
        assertEquals(level, item.level);
        assertNull(item.throwable);
        assertEquals(expectedMessage, item.message);

        assertTrue(item.tag.contains("assertCall(DebugTest.java:"));
    }

    private static Object[] array(Object... elements) {
        return elements;
    }

    private static class LogItem {

        final Level level;
        final Throwable throwable;
        final String tag;
        final String message;

        private LogItem(Level level, Throwable throwable, String tag, String message) {
            this.level = level;
            this.throwable = throwable;
            this.tag = tag;
            this.message = message;
        }
    }

    private static class DebugOutputMock implements DebugOutput {

        private final boolean debug;
        final List<LogItem> logItems = new ArrayList<>();

        private DebugOutputMock(boolean debug) {
            this.debug = debug;
        }

        @Override
        public void log(Level level, Throwable throwable, String tag, String message) {
            logItems.add(new LogItem(level, throwable, tag, message));
        }

        @Override
        public boolean isDebug() {
            return debug;
        }

        int size() {
            return logItems.size();
        }

        LogItem last() {
            return logItems.get(logItems.size() - 1);
        }
    }
}

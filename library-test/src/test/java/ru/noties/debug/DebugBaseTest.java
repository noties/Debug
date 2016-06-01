package ru.noties.debug;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import ru.noties.debug.out.DebugOutput;
import ru.noties.debug.render.DebugRender;
import ru.noties.debug.render.LogItem;
import ru.noties.debug.timer.Timer;
import ru.noties.debug.timer.TimerItem;
import ru.noties.debug.timer.TimerType;

/**
 * Created by Dimitry Ivanov on 11.04.2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DebugBaseTest {

    @Test
    public void testIsDebug() {

        Debug.init(null, null);
        assertFalse(Debug.isDebug());
        Debug.i();

        // initialized with `false` -> must be FALSE
        Debug.init(new DebugOutputFlag(false));
        assertFalse(Debug.isDebug());
        Debug.i();

        // initialized with `true` -> must be TRUE
        Debug.init(new DebugOutputFlag(true));
        assertTrue(Debug.isDebug());
        Debug.i();

        // as there is no renderer -> must be FALSE
        Debug.init(new DebugOutputFlag(true), null);
        assertFalse(Debug.isDebug());
        Debug.i();
    }

    @Test
    public void testTimer() {
        // if `Debug.isDebug` returns FALSE, then EmptyTimer should be returned with no output
        final DebugTimer debugTimer = new DebugTimer(true);
        Debug.init(debugTimer, debugTimer);

        final Timer timer = Debug.newTimer("some name", TimerType.MILLIS);
        timer.start();
        timer.tick("tick message, int: %d", 13);
        timer.stop("stop message");

        debugTimer.timer(timer);
        Debug.i(timer);

        final DebugTimer releaseTimer = new DebugTimer(false);
        Debug.init(releaseTimer, releaseTimer);
        final Timer release = Debug.newTimer("release timer", TimerType.NANO);
        release.start();
        release.tick();
        release.tick("Some other tick");
        release.stop();
        Debug.i(releaseTimer);
    }

    private static class DebugOutputFlag implements DebugOutput {

        private final boolean mIsDebug;

        private DebugOutputFlag(boolean isDebug) {
            mIsDebug = isDebug;
        }

        @Override
        public void log(Level level, Throwable throwable, String tag, String message) {
            assertTrue(mIsDebug);
        }

        @Override
        public boolean isDebug() {
            return mIsDebug;
        }
    }

    private static class DebugRendererAdapter implements DebugRender {

        @Override
        public LogItem log(StackTraceElement element, String message, Object... args) {
            assertTrue(false);
            return null;
        }

        @Override
        public LogItem trace(StackTraceElement[] elements, int maxItems) {
            assertTrue(false);
            return null;
        }

        @Override
        public LogItem timer(StackTraceElement element, String timerName, TimerType timerType, List<TimerItem> timerItems) {
            assertTrue(false);
            return null;
        }
    }

    private static class DebugTimer extends DebugRendererAdapter implements DebugOutput {

        private final boolean mIsDebug;
        private Timer mTimer;

        private DebugTimer(boolean isDebug) {
            mIsDebug = isDebug;
        }

        void timer(Timer timer) {
            this.mTimer = timer;
        }

        @Override
        public void log(Level level, Throwable throwable, String tag, String message) {
            assertTrue(mIsDebug);
        }

        @Override
        public boolean isDebug() {
            return mIsDebug;
        }

        @Override
        public LogItem timer(StackTraceElement element, String timerName, TimerType timerType, List<TimerItem> timerItems) {
            assertTrue(mIsDebug);
            assertEquals(mTimer.getName(), timerName);
            assertEquals(mTimer.getTimerType(), timerType);
            assertEquals(mTimer.getItems(), timerItems);
            return new LogItem(null, null);
        }
    }
}

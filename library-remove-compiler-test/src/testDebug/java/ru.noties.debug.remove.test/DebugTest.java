package ru.noties.debug.remove.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;

@RunWith(RobolectricTestRunner.class)
@Config(application = App.class, manifest = Config.NONE)
public class DebugTest {

    @Test
    public void test() {
        // assert that ALL calls are preserved
        final int expected = DebugCalls.TOTAL_CALLS;
        final int got = DebugCalls.numberOfCalls();
        Assert.assertEquals(
                String.format(Locale.US,"expected calls: %d, got: %d", expected, got),
                expected, got
        );
    }
}
package ru.noties.debug.remove.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = App.class, manifest = Config.NONE)
public class DebugTest {

    @Test
    public void test() {
        // assert that ALL calls are preserved
        Assert.assertEquals(0, DebugCalls.numberOfCalls());
    }
}
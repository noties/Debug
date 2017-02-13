package ru.noties.debug.remove.test;

import ru.noties.debug.Debug;
import ru.noties.debug.DebugOutput;
import ru.noties.debug.Level;

public class DebugCalls {

    private static final int CALLS = 17;
    private static final int BLOCK_CALLS = 4;

    static final int TOTAL_CALLS = CALLS + BLOCK_CALLS;

    static int numberOfCalls() {
        final DebugOutputCounter counter = new DebugOutputCounter();
        App.init(counter);
        new DebugCalls().calls();
        return counter.mNumber;
    }

    private DebugCalls() {}

    private void calls() {
        new Blocks();
        Debug.i();
        staticCall();
        staticCallMultipleLines();
        instanceCall();
        instanceCallMultipleLines();
        innerBlock();
        labeledBlock();
        loops();
        condition();
        traceCall();
    }

    private static void staticCall() {
        Debug.e(new NullPointerException());
    }

    private static void staticCallMultipleLines() {
        final int value = 2 + 2;
        Debug.wtf("%d + %d = %d", value, value, (value + value));
    }

    private void instanceCall() {
        Debug.d();
    }

    private void instanceCallMultipleLines() {
        final String s = "sdfsdfjkuui";
        Debug.i(s, s, s, s, s);
        final String s2 = "sdfs";
    }

    private void innerBlock() {
        final String s;
        {
            s = "inner block";
            Debug.i(s);
            {
                Debug.w();
                {
                    Debug.d();
                    {
                        Debug.e();
                    }
                }
            }
        }
    }

    @SuppressWarnings("UnusedLabel")
    private void labeledBlock() {
        debug: {
            Debug.i();
            release: {
                Debug.e(new Throwable());
            }
        }
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    private void loops() {

        //noinspection ConstantConditions
        for (int i = 0; i < 10; i++) {
            Debug.i();
            break;
        }

        for (String s: new String[] { "", "", "" }) {
            Debug.wtf(s);
            break;
        }

        while (true) {
            Debug.w();
            //noinspection ConstantConditions,ConstantIfStatement
            if (true) {
                break;
            }
        }

        do {
            Debug.i();
        } while (false);
    }

    private void condition() {
        //noinspection StatementWithEmptyBody,ConstantConditions,ConstantIfStatement
        if (true) {
            Debug.e();
        } else {
            ;
        }
    }

    private void traceCall() {
        Debug.trace();
    }

    private static class DebugOutputCounter implements DebugOutput {

        int mNumber;

        @Override
        public void log(Level level, Throwable throwable, String tag, String message) {
            mNumber += 1;
        }

        @Override
        public boolean isDebug() {
            // we will return true
            return true;
        }
    }
}

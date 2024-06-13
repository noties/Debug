package io.noties.debug

import org.junit.Test
import kotlin.test.assertEquals

class PrependCallStackTest {
    @OptIn(PrependCallStack::class)
    @Test
    fun interceptors_prependCallStack() {
        val interceptor = DebugLogInterceptorMock()

        Debug.configure {
            java
                .interceptor {
                    this
                        .prependCallStack()
                        // track created stack
                        .add(interceptor)
                }
        }

        Debug.i("hello!!")

        val callStack = "interceptors_prependCallStack(PrependCallStackTest.kt:22)"
        assertEquals(
            callStack,
            interceptor.logs.first().message.first()
        )
    }
}
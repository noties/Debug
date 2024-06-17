package io.noties.debug.sample.jvm;

import io.noties.debug.Debug;
import io.noties.debug.DebugConfiguration;
import io.noties.debug.DebugConfigurationInterceptor;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainJava {
    public static void main(String[] args) {
        // IDE does not like it, but it compiles and run ok :'(
        Debug.configure(new Function1<DebugConfiguration, Unit>() {
            @Override
            public Unit invoke(DebugConfiguration debugConfiguration) {
                debugConfiguration.getJava()
                        .interceptor(new Function1<DebugConfigurationInterceptor, Unit>() {
                            @Override
                            public Unit invoke(DebugConfigurationInterceptor debugConfigurationInterceptor) {
                                // method not found
//                                debugConfigurationInterceptor.timestamp(s -> Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME));
                                debugConfigurationInterceptor.timestamp(new Function0<String>() {
                                    @Override
                                    public String invoke() {
                                        return Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
                                    }
                                });
                                return Unit.INSTANCE;
                            }
                        });
                return Unit.INSTANCE;
            }
        });

        Debug.shared.v("hello", 42);
        System.out.println("wth");
    }
}

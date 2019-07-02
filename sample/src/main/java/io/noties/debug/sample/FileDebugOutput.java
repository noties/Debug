package io.noties.debug.sample;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.noties.debug.DebugOutput;
import io.noties.debug.Level;

public class FileDebugOutput implements DebugOutput {

    @NonNull
    public static FileDebugOutput create(boolean isDebug, @NonNull File file) {
        return new FileDebugOutput(
                isDebug,
                file,
                Executors.newSingleThreadExecutor(),
                new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)
        );
    }

    private final boolean isDebug;
    private final File file;
    private final Executor executor;
    private final DateFormat dateFormat;

    @SuppressWarnings("WeakerAccess")
    FileDebugOutput(
            boolean isDebug,
            @NonNull File file,
            @NonNull Executor executor,
            @NonNull DateFormat dateFormat) {
        this.isDebug = isDebug;
        this.file = file;
        this.executor = executor;
        this.dateFormat = dateFormat;
    }

    @Override
    public void log(
            @NonNull final Level level,
            @Nullable Throwable throwable,
            @NonNull final String tag,
            @Nullable String message) {

        if (throwable != null) {
            final String trace = throwableStackTrace(throwable);
            if (TextUtils.isEmpty(message)) {
                message = trace;
            } else {
                message = message + "\n" + trace;
            }
        }

        final String out = message != null
                ? message
                : "";

        // we could (I think) also keep filewriter open, but this would require a
        // small de-init timer (that will clear filewriter after some timeout)
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Writer writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(file, true));
                    writer.write(String.format(
                            Locale.US,
                            "%s %s/ %s : %s%n",
                            dateFormat.format(new Date()), level.name(), tag, out
                    ));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            // no op
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }

    @NonNull
    private static String throwableStackTrace(Throwable throwable) {
        final StringWriter writer = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }
}

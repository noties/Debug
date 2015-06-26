package ru.noties.debug.out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.noties.debug.Level;

/**
 * Created by Dimitry Ivanov on 25.06.2015.
 */
public class FileDebugOutput implements DebugOutput {

    public static class UnableToObtainFileException extends Exception {

        public UnableToObtainFileException(String message) {
            super(message);
        }

        public UnableToObtainFileException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public UnableToObtainFileException(Throwable throwable) {
            super(throwable);
        }
    }

    public interface FileStrategy {
        File newSession() throws UnableToObtainFileException;
    }

    public interface OutputConverter {
        String convert(Level level, Throwable throwable, String tag, String message);

        class DefaultOutputConverter implements OutputConverter {

            private static final String PATTERN = "%s  %s  %s/%s: %s";
            private static final String DATE_PATTERN = "dd-MM HH:mm:ss.SSS";

            private final DateFormat dateFormat;
            {
                dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
            }

            @Override
            public String convert(Level level, Throwable throwable, String tag, String message) {

                String out = String.format(
                        PATTERN,
                        dateFormat.format(new Date()),
                        Thread.currentThread().getName(),
                        level.name(),
                        tag,
                        message
                );

                if (throwable != null) {
                    out += OutputUtils.convert(throwable);
                }

                return out;
            }
        }
    }

    protected static void write(File file, String text) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                //noinspection EmptyCatchBlock
                try {
                    writer.close();
                } catch (IOException e) {}
            }
        }
    }

    public static FileDebugOutput newInstance(
            boolean isDebug,
            boolean isAsync,
            FileStrategy fileStrategy
    ) throws UnableToObtainFileException {
        return FileDebugOutput.newInstance(isDebug, isAsync, fileStrategy, new OutputConverter.DefaultOutputConverter());
    }

    public static FileDebugOutput newInstance(
            boolean isDebug,
            boolean isAsync,
            FileStrategy fileStrategy,
            OutputConverter outputConverter
    ) throws UnableToObtainFileException {
        final File file = fileStrategy.newSession();
        return new FileDebugOutput(isDebug, isAsync, file, outputConverter);
    }

    private final boolean isDebug;
    private final ExecutorService executorService;
    private final File logFile;
    private final OutputConverter outputConverter;

    private FileDebugOutput(boolean isDebug, boolean isAsync, File logFile, OutputConverter outputConverter) {
        this.isDebug = isDebug;
        this.logFile = logFile;
        this.outputConverter = outputConverter;

        if (isAsync) {
            this.executorService = Executors.newFixedThreadPool(1);
        } else {
            this.executorService = null;
        }
    }

    @Override
    public void log(Level level, Throwable throwable, String tag, String message) {
        final String out = outputConverter.convert(level, throwable, tag, message);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                write(logFile, out);
            }
        };

        if (executorService != null) {
            executorService.submit(runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }
}

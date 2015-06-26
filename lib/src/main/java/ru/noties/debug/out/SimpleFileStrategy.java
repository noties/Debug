package ru.noties.debug.out;

import java.io.File;
import java.io.IOException;

/**
 * Created by Dimitry Ivanov on 25.06.2015.
 */
public class SimpleFileStrategy implements FileDebugOutput.FileStrategy {

    public static class InitializationException extends Exception {

        public InitializationException(String message) {
            super(message);
        }
    }

    public interface LogFileNameStrategy {
        String create();
    }

    public static SimpleFileStrategy newInstance(
            File folder,
            String logFolderName,
            LogFileNameStrategy logFileNameStrategy
    ) throws InitializationException {
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new InitializationException("Could not obtain application's cache dir, path: " + folder.getAbsolutePath());
            }
        }
        final File logsFolder = new File(folder, logFolderName);
        if (!logsFolder.exists()) {
            if (!logsFolder.mkdirs()) {
                throw new InitializationException("Could not obtain logs folder, path: " + logsFolder.getAbsolutePath());
            }
        }

        return new SimpleFileStrategy(logsFolder, logFileNameStrategy);
    }

    private final File logsFolder;
    private final LogFileNameStrategy logFileNameStrategy;

    private SimpleFileStrategy(File logFolder, LogFileNameStrategy logFileNameStrategy) {
        this.logsFolder = logFolder;
        this.logFileNameStrategy = logFileNameStrategy;
    }

    @Override
    public File newSession() throws FileDebugOutput.UnableToObtainFileException {
        final File file = new File(logsFolder, logFileNameStrategy.create());
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    return file;
                }
                throw new FileDebugOutput.UnableToObtainFileException("Could not create new file, path: " + file.getAbsolutePath());
            } catch (IOException e) {
                throw new FileDebugOutput.UnableToObtainFileException("Could not create new file, path: " + file.getAbsolutePath(), e);
            }
        }
        return file;
    }
}

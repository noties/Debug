package io.noties.debug.sample;

import android.app.Application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import io.noties.debug.AndroidLogDebugOutput;
import io.noties.debug.Debug;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final File file = new File(getFilesDir(), "debug.log");

        // let's clear the previous logs just for simplicity
        if (file.exists() && file.length() > 0) {
            Writer writer = null;
            try {
                writer = new FileWriter(file, false);
                writer.write("");
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

        Debug.init(
                AndroidLogDebugOutput.create(BuildConfig.DEBUG),
                FileDebugOutput.create(BuildConfig.DEBUG, file)
        );
    }
}

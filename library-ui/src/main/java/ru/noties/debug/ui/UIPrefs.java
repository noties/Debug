package ru.noties.debug.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import ru.noties.debug.Level;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
class UIPrefs {

    private static final String KEY_LEVEL = "level";
    private static final String KEY_TAG = "tag";

    private final SharedPreferences mPrefs;
    private final SharedPreferences.Editor mEditor;

    @SuppressLint("CommitPrefEdits")
    public UIPrefs(Context context) {
        mPrefs = context.getSharedPreferences("debug_ui_prefs", Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

    public void setLevel(Level level) {
        final int l = level == null ? -1 : level.ordinal();
        mEditor.putInt(KEY_LEVEL, l).apply();
    }

    public Level getLevel() {
        final int level = mPrefs.getInt(KEY_LEVEL, -1);
        if (level == -1) {
            return null;
        }
        return Level.values()[level];
    }

    public void setTag(String tag) {
        mEditor.putString(KEY_TAG, tag).apply();
    }

    public String getTag() {
        return mPrefs.getString(KEY_TAG, null);
    }
}

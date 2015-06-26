package ru.noties.debug.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

import ru.noties.debug.Level;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class LogItemFilter implements Parcelable {

    private Level level;
    private String tag;

    public Level getLevel() {
        return level;
    }

    public LogItemFilter setLevel(Level level) {
        this.level = level;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public LogItemFilter setTag(String tag) {
        this.tag = tag;
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.level == null ? -1 : this.level.ordinal());
        dest.writeString(this.tag);
    }

    public LogItemFilter() {
    }

    protected LogItemFilter(Parcel in) {
        int tmpLevel = in.readInt();
        this.level = tmpLevel == -1 ? null : Level.values()[tmpLevel];
        this.tag = in.readString();
    }

    public static final Parcelable.Creator<LogItemFilter> CREATOR = new Parcelable.Creator<LogItemFilter>() {
        public LogItemFilter createFromParcel(Parcel source) {
            return new LogItemFilter(source);
        }

        public LogItemFilter[] newArray(int size) {
            return new LogItemFilter[size];
        }
    };
}

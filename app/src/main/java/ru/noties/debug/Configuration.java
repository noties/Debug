package ru.noties.debug;

/**
 * Created by dimaster on 17.08.14.
 */
public class Configuration {

    private static final String DEFAULT_TAG = "Debug";

    private final String mTag;
    private final boolean mDebug;

    private Configuration(Builder builder) {

        this.mTag       = builder.tag;
        this.mDebug     = builder.isDebug;

    }

    public String getTag() {
        return mTag;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public static class Builder {

        private String tag = DEFAULT_TAG;
        private boolean isDebug;

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setDebug(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }
}

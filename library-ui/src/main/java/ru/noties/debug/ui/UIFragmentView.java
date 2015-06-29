package ru.noties.debug.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;

import ru.noties.debug.Level;
import ru.noties.debug.ui.model.LogItem;
import ru.noties.debug.ui.model.LogItemFilter;
import ru.noties.debug.ui.model.UIFragmentAdapter;
import ru.noties.storm.StormIterator;
import ru.noties.storm.adapter.BaseStormIteratorAdapter;
import ru.noties.storm.pool.ObjectPool;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class UIFragmentView implements Parcelable {

    public interface OnFilterListener {
        void onFilterChange(LogItemFilter filter);
    }

    private static final String ARG_SAVED_STATE = "arg.SavedState";

    private ListView mListView;
    private BaseStormIteratorAdapter<LogItem> mAdapter;
    private OnFilterListener mOnFilterListener;

    private int[] mSavedState;
    private boolean mIsInitialLoad;
    private LogItemFilter mFilter;
    private UIPrefs mPrefs;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle sis) {

        mPrefs = new UIPrefs(inflater.getContext());
        final Level level = mPrefs.getLevel();
        final String tag = mPrefs.getTag();
        if (level != null
                || tag != null) {
            mFilter = new LogItemFilter()
                    .setTag(tag)
                    .setLevel(level);
        }

        final View view = inflater.inflate(R.layout.fragment_view, parent, false);
        mListView = (ListView) view.findViewById(R.id.debug_ui_list_view);
        mListView.setEmptyView(view.findViewById(R.id.debug_list_view_empty_view));
        mAdapter = new UIFragmentAdapter(inflater.getContext(), new LogItem[50]);
        mListView.setAdapter(mAdapter);

        if (sis != null) {
            mSavedState = sis.getIntArray(ARG_SAVED_STATE);
        } else {
            mIsInitialLoad = true;
        }

        final TextView levelFilter = (TextView) view.findViewById(R.id.debug_view_level_filter);
        initLevelFilter(levelFilter);

        final EditText tagFilter = (EditText) view.findViewById(R.id.debug_view_tag_filter);
        initTagFilter(tagFilter);

        return view;
    }

    private void initLevelFilter(final TextView textView) {
        textView.setText(getFilterString());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Context context = v.getContext();
                final ListPopupWindow popupWindow = new ListPopupWindow(context);
                final int popupWindowWidth = context.getResources().getDimensionPixelSize(R.dimen.debug_pop_up_window_width);
                popupWindow.setAnchorView(textView);
                popupWindow.setWidth(popupWindowWidth);
                popupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
                popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String value = (String) parent.getAdapter().getItem(position);
                        Level level;
                        try {
                            level = Level.valueOf(value);
                        } catch (IllegalArgumentException e) {
                            level = null;
                        }
                        if (updateFilter(level)) {
                            textView.setText(getFilterString());
                            updateLevel(level);
                            sendUpdatedFilter();
                        }
                        popupWindow.dismiss();
                    }
                });
                final Level[] levels = Level.values();
                final String[] items = new String[levels.length + 1];
                for (int i = 0, size = levels.length; i < size; i++) {
                    items[i] = levels[i].name();
                }
                items[items.length - 1] = "All";
                popupWindow.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, items));
                popupWindow.show();
            }
        });
    }

    private void initTagFilter(EditText editText) {
        final String tag = mFilter != null ? mFilter.getTag() : null;
        if (tag != null) {
            editText.setText(tag);
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String text = s.length() == 0 ? null : s.toString();
                if (updateFilter(text)) {
                    updateTag(text);
                    sendUpdatedFilter();
                }
            }
        });
    }

    private String getFilterString() {
        final Level level = mFilter == null ? null : mFilter.getLevel();
        if (level == null) {
            return "ALL";
        }
        return level.name();
    }

    private boolean updateFilter(Level level) {
        if (level == null) {

            if (mFilter == null) {
                return false;
            }

            if (mFilter.getTag() == null) {
                mFilter = null;
            } else {
                mFilter.setLevel(null);
            }
            return true;
        }

        final Level current = mFilter == null ? null : mFilter.getLevel();
        if (level.equals(current)) {
            return false;
        }

        if (mFilter == null) {
            mFilter = new LogItemFilter();
        }

        mFilter.setLevel(level);
        return true;
    }

    private boolean updateFilter(String tag) {
        if (tag == null) {
            if (mFilter == null) {
                return false;
            }

            if (mFilter.getLevel() == null) {
                mFilter = null;
            } else {
                mFilter.setTag(null);
            }
            return true;
        }

        final String current = mFilter == null ? null : mFilter.getTag();
        if (tag.equals(current)) {
            return false;
        }

        if (mFilter == null) {
            mFilter = new LogItemFilter();
        }

        mFilter.setTag(tag);
        return true;
    }

    private void updateTag(String tag) {
        mPrefs.setTag(tag);
    }

    private void updateLevel(Level level) {
        mPrefs.setLevel(level);
    }

    private void sendUpdatedFilter() {
        if (mOnFilterListener != null) {
            mOnFilterListener.onFilterChange(mFilter);
        }
    }

    public void onSaveInstanceState(Bundle out) {
        if (mListView != null) {
            final int  firstPosition = mListView.getFirstVisiblePosition();
            final View view = mListView.getChildAt(0);
            out.putIntArray(ARG_SAVED_STATE, new int[] { firstPosition, view != null ? view.getTop() : 0 });
        }
    }

    public void setIterator(StormIterator<LogItem> iterator) {
        if (mAdapter != null) {

            if (iterator == null) {
                iterator = new DummyIterator<>();
            }

            mAdapter.setIterator(iterator, true, true);
            if (mSavedState != null) {
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setSelectionFromTop(mSavedState[0], mSavedState[1]);
                        mSavedState = null;
                    }
                });
            } else if (mIsInitialLoad) {
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setSelection(mAdapter.getCount() - 1);
                        mIsInitialLoad = false;
                    }
                });
            }
        }
    }

    public void setOnFilterListener(OnFilterListener listener) {
        this.mOnFilterListener = listener;
        if (mFilter != null
                && mOnFilterListener != null) {
            mOnFilterListener.onFilterChange(mFilter);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public UIFragmentView() {
    }

    protected UIFragmentView(Parcel in) {
    }

    public static final Parcelable.Creator<UIFragmentView> CREATOR = new Parcelable.Creator<UIFragmentView>() {
        public UIFragmentView createFromParcel(Parcel source) {
            return new UIFragmentView(source);
        }

        public UIFragmentView[] newArray(int size) {
            return new UIFragmentView[size];
        }
    };

    private static class DummyIterator<T> implements StormIterator<T> {

        @Override
        public int getCount() {
            return 0;
        }

        @Nullable
        @Override
        public T get(int i) {
            return null;
        }

        @Override
        public void close() {

        }

        @Override
        public void setObjectPool(ObjectPool<T> objectPool) {

        }

        @Nullable
        @Override
        public ObjectPool<T> getObjectPool() {
            return null;
        }
    }
}

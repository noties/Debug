package ru.noties.debug.ui;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ru.noties.debug.ui.model.LogItem;
import ru.noties.debug.ui.model.UIFragmentAdapter;
import ru.noties.storm.StormIterator;
import ru.noties.storm.adapter.BaseStormIteratorAdapter;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class UIFragmentView implements Parcelable {

    private static final String ARG_SAVED_STATE = "arg.SavedState";

    private ListView mListView;
    private BaseStormIteratorAdapter<LogItem> mAdapter;

    private int[] mSavedState;
    private boolean mIsInitialLoad;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle sis) {
        final View view = inflater.inflate(R.layout.fragment_view, parent, false);
        mListView = (ListView) view.findViewById(R.id.debug_ui_list_view);
        mAdapter = new UIFragmentAdapter(inflater.getContext(), new LogItem[50]);
        mListView.setAdapter(mAdapter);

        if (sis != null) {
            mSavedState = sis.getIntArray(ARG_SAVED_STATE);
        } else {
            mIsInitialLoad = true;
        }

        return view;
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
}

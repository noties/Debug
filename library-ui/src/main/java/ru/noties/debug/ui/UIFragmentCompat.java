package ru.noties.debug.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.debug.ui.model.LogDatabase;
import ru.noties.debug.ui.model.LogItem;
import ru.noties.debug.ui.model.LogLoaderCompat;
import ru.noties.storm.StormIterator;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class UIFragmentCompat extends Fragment implements LoaderManager.LoaderCallbacks<StormIterator<LogItem>> {

    private static final String ARG_VIEW = "arg.View";

    public static UIFragmentCompat newInstance(UIFragmentView view) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_VIEW, view);

        final UIFragmentCompat fragment = new UIFragmentCompat();
        fragment.setArguments(bundle);
        return fragment;
    }

    private UIFragmentView mView;

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);

        mView = getArguments().getParcelable(ARG_VIEW);
    }

    protected UIFragmentView getUIView() {
        return mView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle sis) {
        final UIFragmentView fragmentView = getUIView();
        if (fragmentView == null) {
            return null;
        }
        return fragmentView.onCreateView(inflater, parent, sis);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getUIView().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStop() {
        super.onStop();

//        getUIView().setIterator(null);
        getLoaderManager().destroyLoader(0);
    }


    @Override
    public Loader<StormIterator<LogItem>> onCreateLoader(int id, Bundle args) {
        return new LogLoaderCompat(getActivity(), LogDatabase.getInstance().open());
    }

    @Override
    public void onLoadFinished(Loader<StormIterator<LogItem>> loader, StormIterator<LogItem> data) {
        getUIView().setIterator(data);
    }

    @Override
    public void onLoaderReset(Loader<StormIterator<LogItem>> loader) {

    }
}

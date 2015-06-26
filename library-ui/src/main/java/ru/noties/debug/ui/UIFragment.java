package ru.noties.debug.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.noties.debug.ui.model.LogDatabase;
import ru.noties.debug.ui.model.LogItem;
import ru.noties.debug.ui.model.LogItemFilter;
import ru.noties.debug.ui.model.LogLoader;
import ru.noties.storm.StormIterator;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class UIFragment extends Fragment implements LoaderManager.LoaderCallbacks<StormIterator<LogItem>> {

    private static final String ARG_VIEW = "arg.View";

    public static UIFragment newInstance(UIFragmentView view) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_VIEW, view);

        final UIFragment fragment = new UIFragment();
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
        final View view = fragmentView.onCreateView(inflater, parent, sis);
        fragmentView.setOnFilterListener(new UIFragmentView.OnFilterListener() {
            @Override
            public void onFilterChange(LogItemFilter filter) {
                final Loader<StormIterator<LogItem>> loader = getLoaderManager().getLoader(0);
                if (loader != null) {
                    ((LogLoader) loader).setLogFilter(filter);
                }
            }
        });
        return view;
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
        return new LogLoader(getActivity(), LogDatabase.getInstance().open());
    }

    @Override
    public void onLoadFinished(Loader<StormIterator<LogItem>> loader, StormIterator<LogItem> data) {
        getUIView().setIterator(data);
    }

    @Override
    public void onLoaderReset(Loader<StormIterator<LogItem>> loader) {

    }
}

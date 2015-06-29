package ru.noties.debug.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Dimitry Ivanov on 29.06.2015.
 */
public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle sis) {
        final TextView textView = new TextView(inflater.getContext());
        textView.setText(getClass().getCanonicalName());
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}

package ru.noties.debug.ui.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.noties.debug.ui.R;
import ru.noties.storm.adapter.BaseStormIteratorAdapter;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class UIFragmentAdapter extends BaseStormIteratorAdapter<LogItem> {

    protected static final String CONVERT_PATTERN = "%s  %s/%s: %s";

    public UIFragmentAdapter(Context context, LogItem[] poolArray) {
        super(context, poolArray);
    }

    @Override
    protected View newView(LayoutInflater layoutInflater, int i, ViewGroup viewGroup) {
        final View view = layoutInflater.inflate(R.layout.adapter_log_item, viewGroup, false);
        view.setTag(new Holder(view));
        return view;
    }

    @Override
    protected void bindView(int i, View view) {
        final LogItem item = getItem(i);
        final Holder holder = (Holder) view.getTag();
        holder.text.setText(convertItem(item));
    }

    protected String convertItem(LogItem item) {
        return String.format(CONVERT_PATTERN, item.getDate(), item.getLevel(), item.getTag(), item.getMessage());
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    protected static class Holder {
        private final TextView text;
        protected Holder(View view) {
            this.text = (TextView) view.findViewById(R.id.debug_ui_adapter_text);
        }
    }
}

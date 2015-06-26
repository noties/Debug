package ru.noties.debug.ui.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.noties.debug.Level;
import ru.noties.debug.ui.R;
import ru.noties.storm.adapter.BaseStormIteratorAdapter;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class UIFragmentAdapter extends BaseStormIteratorAdapter<LogItem> {

    protected static final String CONVERT_PATTERN = "%s  %s/%s: %s";
    private static final String DATE_PATTERN = "dd-MM HH:mm:ss.SSS";

    private final DateFormat mFormat;
    private final int[] mColors;

    public UIFragmentAdapter(Context context, LogItem[] poolArray) {
        super(context, poolArray);
        mFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        mColors = new int[] {
                context.getResources().getColor(R.color.debug_text_color_main),
                context.getResources().getColor(R.color.debug_text_color_level_error)
        };
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
        holder.text.setTextColor(mColors[item.getLevel() == Level.E ? 1 : 0]);
        holder.text.setText(convertItem(item));
    }

    protected String convertItem(LogItem item) {
        final String date = mFormat.format(new Date(item.getDate()));
        return String.format(CONVERT_PATTERN, date, item.getLevel(), item.getTag(), item.getMessage());
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

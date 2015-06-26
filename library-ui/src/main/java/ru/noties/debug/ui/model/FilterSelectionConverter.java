package ru.noties.debug.ui.model;

import ru.noties.storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class FilterSelectionConverter {

    private FilterSelectionConverter() {}

    public static Selection convert(LogItemFilter filter) {
        Selection selection = null;
        if (filter.getLevel() != null) {
            selection = Selection.eq(LogItem.COL_LEVEL, filter.getLevel().ordinal());
        }
        final String tag = filter.getTag();
        if (tag != null) {
            final Selection likeSelection = new LikeSelection(LogItem.COL_TAG, tag);
            if (selection == null) {
                selection = likeSelection;
            } else {
                selection.and(likeSelection);
            }
        }
        return selection;
    }

    private static class LikeSelection extends Selection {

        public LikeSelection(String key, String value) {
            super(key, " LIKE ", String.format("%%%s%%", value));
        }

    }
}

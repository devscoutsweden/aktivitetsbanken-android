package se.devscout.android.view.widget;

import java.io.Serializable;

abstract class AbstractComponentFactory implements Serializable {

    private final String mUniqueName;

    public String getId() {
        return mUniqueName;
    }

    private final int mNameResId;
    private final int mIconResId;
    private final boolean mWidgetTitleImportant;

    AbstractComponentFactory(String uniqueName, int nameResId, int iconResId) {
        this(uniqueName, nameResId, iconResId, false);
    }

    AbstractComponentFactory(String uniqueName, int nameResId, int iconResId, boolean widgetTitleImportant) {
        mUniqueName = uniqueName;
        mIconResId = iconResId;
        mNameResId = nameResId;
        mWidgetTitleImportant = widgetTitleImportant;
    }

    public boolean isWidgetTitleImportant() {
        return mWidgetTitleImportant;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public int getTitleResId() {
        return mNameResId;
    }

}

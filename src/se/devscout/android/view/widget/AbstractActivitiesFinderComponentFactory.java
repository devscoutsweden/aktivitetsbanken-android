package se.devscout.android.view.widget;

import java.io.Serializable;

public abstract class AbstractActivitiesFinderComponentFactory implements Serializable {

    private String mUniqueName;

    public String getId() {
        return mUniqueName;
    }

    private int mNameResId;
    private int mIconResId;
    private boolean mWidgetTitleImportant;

    protected AbstractActivitiesFinderComponentFactory(String uniqueName, int nameResId, int iconResId) {
        this(uniqueName, nameResId, iconResId, false);
    }

    protected AbstractActivitiesFinderComponentFactory(String uniqueName, int nameResId, int iconResId, boolean widgetTitleImportant) {
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

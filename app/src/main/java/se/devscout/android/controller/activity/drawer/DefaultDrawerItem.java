package se.devscout.android.controller.activity.drawer;

public abstract class DefaultDrawerItem implements DrawerItem {
    private final String mTitle;
    private final int mIconResId;

    DefaultDrawerItem(String title) {
        this(title, 0);
    }

    DefaultDrawerItem(String title, int iconResId) {
        mTitle = title;
        mIconResId = iconResId;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int getIconResourceId() {
        return mIconResId;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}

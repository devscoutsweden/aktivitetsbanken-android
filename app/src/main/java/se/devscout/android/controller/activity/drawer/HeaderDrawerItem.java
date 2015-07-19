package se.devscout.android.controller.activity.drawer;

public class HeaderDrawerItem extends DefaultDrawerItem {

    public HeaderDrawerItem(String title) {
        super(title);
    }

    @Override
    public boolean isHeader() {
        return true;
    }
}

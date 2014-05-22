package se.devscout.android.controller.activity.drawer;

import java.io.Serializable;

public interface DrawerItem extends Serializable {
    String getTitle();

    String getDescription();

    int getIconResourceId();

    boolean isHeader();
}

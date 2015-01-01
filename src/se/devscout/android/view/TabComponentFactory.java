package se.devscout.android.view;

import android.support.v4.app.Fragment;

import java.io.Serializable;

public interface TabComponentFactory extends Serializable {
    String getId();

    int getIconResId();

    int getTitleResId();

    Fragment createFragment();
}

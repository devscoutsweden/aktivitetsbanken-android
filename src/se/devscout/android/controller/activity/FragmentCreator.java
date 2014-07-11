package se.devscout.android.controller.activity;

import android.support.v4.app.Fragment;

import java.io.Serializable;

public interface FragmentCreator extends Serializable {
    Fragment createFragment();
}

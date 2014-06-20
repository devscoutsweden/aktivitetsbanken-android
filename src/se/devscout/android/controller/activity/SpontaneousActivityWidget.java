package se.devscout.android.controller.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.view.FindSpontaneousActivitiesView;

public class SpontaneousActivityWidget implements StartScreenWidget {
    @Override
    public int getTitleResId() {
        return R.string.startTabSpontaneousActivity;
    }

    @Override
    public View[] getViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new View[]{new FindSpontaneousActivitiesView(container.getContext(), true)};
    }

    @Override
    public void onFragmentResume() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

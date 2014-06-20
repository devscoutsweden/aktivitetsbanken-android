package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import se.devscout.android.AgeGroup;
import se.devscout.android.view.AgeGroupListView;

public class AgeGroupListFragment extends QuickSearchListFragment<AgeGroup, AgeGroupListView> {

    public AgeGroupListFragment() {
        super(AgeGroup.values());
    }

    public static AgeGroupListFragment create() {
        AgeGroupListFragment fragment = new AgeGroupListFragment();
        return fragment;
    }

    @Override
    protected AgeGroupListView createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new AgeGroupListView(getActivity(), 0, 0, false);
    }
}

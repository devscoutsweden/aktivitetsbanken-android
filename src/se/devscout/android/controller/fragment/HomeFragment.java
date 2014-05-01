package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.model.repo.SQLiteActivityRepo;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);
        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.homeList);
        if (fragment == null) {
            fragment = FeaturedActivitiesListFragment.create(SQLiteActivityRepo.getInstance(getActivity()));
            fm.beginTransaction().add(R.id.homeList, fragment).commit();
        }
        return view;
    }

    public static HomeFragment create() {
        return new HomeFragment();
    }
}

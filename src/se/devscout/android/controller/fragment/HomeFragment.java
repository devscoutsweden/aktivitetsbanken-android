package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import se.devscout.android.R;
import se.devscout.android.model.repo.SQLiteActivityRepo;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);
        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.homeList);
        if (fragment == null) {
            fragment = FeaturedActivitiesListFragment.create(SQLiteActivityRepo.getInstance(getActivity()));// AgeGroupListFragment.create();
            fm.beginTransaction().add(R.id.homeList, fragment).commit();
        }
        TextView introTextView = (TextView) view.findViewById(R.id.homeIntroText);
        introTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Resetting database", Toast.LENGTH_SHORT).show();
                SQLiteActivityRepo.getInstance(getActivity()).resetDatabase(true);
                Toast.makeText(getActivity(), "Database has been reset", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public static HomeFragment create() {
        return new HomeFragment();
    }
}

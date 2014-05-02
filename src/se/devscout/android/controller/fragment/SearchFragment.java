package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ToggleButton;
import se.devscout.android.AgeGroup;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SearchResultActivity;
import se.devscout.android.util.*;

public class SearchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View searchView = inflater.inflate(R.layout.search_activity, container, false);
        Button button = (Button) searchView.findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndFilter filter = new AndFilter();

                initFeaturedOnlyFilter(filter);

                initTextFilter(filter);

                initAgeFilter(filter);

                startActivity(SearchResultActivity.createIntent(getActivity(), filter, getString(R.string.searchResultTitle)));
            }

            private void initAgeFilter(AndFilter filter) {
                OrFilter ageFilter = new OrFilter();
                addAgeFilter(ageFilter, R.id.searchAgeAdventurer, AgeGroup.ADVENTURER);
                addAgeFilter(ageFilter, R.id.searchAgeChallenger, AgeGroup.CHALLENGER);
                addAgeFilter(ageFilter, R.id.searchAgeDiscoverer, AgeGroup.DISCOVERER);
                addAgeFilter(ageFilter, R.id.searchAgeRover, AgeGroup.ROVER);
                addAgeFilter(ageFilter, R.id.searchAgeTracker, AgeGroup.TRACKER);

                if (!ageFilter.isEmpty()) {
                    filter.addFilter(ageFilter);
                }
            }

            private void initTextFilter(AndFilter filter) {
                EditText searchEditText = (EditText) searchView.findViewById(R.id.searchText);
                String searchText = searchEditText.getText().toString();
                if (searchText.length() > 0) {
                    filter.addFilter(new TextFilter(searchText));
                }
            }

            private void initFeaturedOnlyFilter(AndFilter filter) {
                CheckBox featuredOnlyCheckbox = (CheckBox) searchView.findViewById(R.id.searchFeaturedOnly);
                if (featuredOnlyCheckbox.isChecked()) {
                    filter.addFilter(new IsFeaturedFilter());
                }
            }

            private void addAgeFilter(OrFilter ageFilter, int toggleButtonResId, AgeGroup ageGroup) {
                ToggleButton toggleButton = (ToggleButton) searchView.findViewById(toggleButtonResId);
                if (toggleButton.isChecked()) {
                    ageFilter.addFilter(new AgeRangeFilter(ageGroup.getScoutAgeRange()));
                }
            }
        });
        return searchView;
    }

    public static SearchFragment create() {
        return new SearchFragment();
    }
}

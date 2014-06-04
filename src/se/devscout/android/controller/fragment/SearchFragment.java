package se.devscout.android.controller.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import se.devscout.android.AgeGroup;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SearchResultActivity;
import se.devscout.android.model.IntegerRangePojo;
import se.devscout.server.api.ActivityFilterFactory;
import se.devscout.server.api.ActivityFilterFactoryException;

public class SearchFragment extends ActivityBankFragment {

    private TimeRange[] mTimeRanges;

    private static class TimeRange extends IntegerRangePojo {
        private String label;

        private TimeRange(int min, int max, String label) {
            super(min, max);
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    public void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        View view = getView();
        SharedPreferences.Editor editor = getPreferences().edit();

        editor.putInt("searchTimeSpinner", ((Spinner) view.findViewById(R.id.searchTimeSpinner)).getSelectedItemPosition());
        editor.putString("searchText", ((TextView) view.findViewById(R.id.searchText)).getText().toString());
        editor.putBoolean("searchAgeAdventurer", ((CheckBox) view.findViewById(R.id.searchAgeAdventurer)).isChecked());
        editor.putBoolean("searchAgeChallenger", ((CheckBox) view.findViewById(R.id.searchAgeChallenger)).isChecked());
        editor.putBoolean("searchAgeDiscoverer", ((CheckBox) view.findViewById(R.id.searchAgeDiscoverer)).isChecked());
        editor.putBoolean("searchAgeRover", ((CheckBox) view.findViewById(R.id.searchAgeRover)).isChecked());
        editor.putBoolean("searchAgeTracker", ((CheckBox) view.findViewById(R.id.searchAgeTracker)).isChecked());
        editor.putBoolean("featuredOnlyCheckbox", ((CheckBox) view.findViewById(R.id.searchFeaturedOnly)).isChecked());
        editor.putBoolean("searchFavouritesOnlyCheckbox", ((CheckBox) view.findViewById(R.id.searchFavouritesOnly)).isChecked());

        editor.commit();
    }

    public void loadSavedValues(View view) {
        ((Spinner) view.findViewById(R.id.searchTimeSpinner)).setSelection(Math.min(getPreferences().getInt("searchTimeSpinner", 0), mTimeRanges.length - 1));
        ((TextView) view.findViewById(R.id.searchText)).setText(getPreferences().getString("searchText", ""));
        ((CheckBox) view.findViewById(R.id.searchAgeAdventurer)).setChecked(getPreferences().getBoolean("searchAgeAdventurer", false));
        ((CheckBox) view.findViewById(R.id.searchAgeChallenger)).setChecked(getPreferences().getBoolean("searchAgeChallenger", false));
        ((CheckBox) view.findViewById(R.id.searchAgeDiscoverer)).setChecked(getPreferences().getBoolean("searchAgeDiscoverer", false));
        ((CheckBox) view.findViewById(R.id.searchAgeRover)).setChecked(getPreferences().getBoolean("searchAgeRover", false));
        ((CheckBox) view.findViewById(R.id.searchAgeTracker)).setChecked(getPreferences().getBoolean("searchAgeTracker", false));
        ((CheckBox) view.findViewById(R.id.searchFeaturedOnly)).setChecked(getPreferences().getBoolean("featuredOnlyCheckbox", false));
        ((CheckBox) view.findViewById(R.id.searchFavouritesOnly)).setChecked(getPreferences().getBoolean("searchFavouritesOnlyCheckbox", false));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View searchView = inflater.inflate(R.layout.search_activity, container, false);

        mTimeRanges = new TimeRange[]{
                new TimeRange(0, Integer.MAX_VALUE, getString(R.string.searchTimeOptionAny)),
                new TimeRange(0, 5, getString(R.string.searchTimeOption5min)),
                new TimeRange(0, 15, getString(R.string.searchTimeOption15min)),
                new TimeRange(10, 35, getString(R.string.searchTimeOption15to30min)),
                new TimeRange(25, 65, getString(R.string.searchTimeOption30to60min)),
                new TimeRange(50, 140, getString(R.string.searchTimeOption1to2hours)),
                new TimeRange(120, Integer.MAX_VALUE, getString(R.string.searchTimeOptionMoreThan2hours))};

        final Spinner spinner = initTimeDropDown(searchView);

        initAdditionalCheckboxLabel(searchView, R.id.searchAgeAdventurerLogo, R.id.searchAgeAdventurer);
        initAdditionalCheckboxLabel(searchView, R.id.searchAgeChallengerLogo, R.id.searchAgeChallenger);
        initAdditionalCheckboxLabel(searchView, R.id.searchAgeDiscovererLogo, R.id.searchAgeDiscoverer);
        initAdditionalCheckboxLabel(searchView, R.id.searchAgeRoverLogo, R.id.searchAgeRover);
        initAdditionalCheckboxLabel(searchView, R.id.searchAgeTrackerLogo, R.id.searchAgeTracker);

        loadSavedValues(searchView);

        Button button = (Button) searchView.findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                try {
                    ActivityFilterFactory mFilterFactory = getActivityBank().getFilterFactory();
                    se.devscout.server.api.activityfilter.AndFilter filter = mFilterFactory.createAndFilter();

                    initFeaturedOnlyFilter(filter, mFilterFactory);

                    initFavouritesOnlyFilter(filter, mFilterFactory);

                    initTextFilter(filter, mFilterFactory);

                    initAgeFilter(filter, mFilterFactory);

                    initTimeRange(filter, mFilterFactory);

                    startActivity(SearchResultActivity.createIntent(getActivity(), filter, getString(R.string.searchResultTitle)));
                } catch (ActivityFilterFactoryException e) {
//                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(SearchFragment.class.getName(), "Could not create activity filter.", e);
                }
            }

            private void initTimeRange(se.devscout.server.api.activityfilter.AndFilter filter, ActivityFilterFactory mFilterFactory) {
                TimeRange selectedTimeRange = (TimeRange) spinner.getSelectedItem();
                if (selectedTimeRange != null && selectedTimeRange.getMin() >= 0 && selectedTimeRange.getMax() < Integer.MAX_VALUE) {
                    // A time range is selected and it is not the "any-option".
                    se.devscout.server.api.activityfilter.TimeRangeFilter filter1 = mFilterFactory.createTimeRangeFilter(selectedTimeRange);
                    filter.getFilters().add(filter1);
                }
            }

            private void initAgeFilter(se.devscout.server.api.activityfilter.AndFilter filter, ActivityFilterFactory mFilterFactory) {
                se.devscout.server.api.activityfilter.OrFilter ageFilter = mFilterFactory.createOrFilter();
                addAgeFilter(ageFilter, R.id.searchAgeAdventurer, AgeGroup.ADVENTURER, mFilterFactory);
                addAgeFilter(ageFilter, R.id.searchAgeChallenger, AgeGroup.CHALLENGER, mFilterFactory);
                addAgeFilter(ageFilter, R.id.searchAgeDiscoverer, AgeGroup.DISCOVERER, mFilterFactory);
                addAgeFilter(ageFilter, R.id.searchAgeRover, AgeGroup.ROVER, mFilterFactory);
                addAgeFilter(ageFilter, R.id.searchAgeTracker, AgeGroup.TRACKER, mFilterFactory);

                if (!ageFilter.getFilters().isEmpty()) {
                    filter.getFilters().add(ageFilter);
                }
            }

            private void initTextFilter(se.devscout.server.api.activityfilter.AndFilter filter, ActivityFilterFactory mFilterFactory) {
                EditText searchEditText = (EditText) searchView.findViewById(R.id.searchText);
                String searchText = searchEditText.getText().toString();
                if (searchText.length() > 0) {
                    filter.getFilters().add(mFilterFactory.createTextFilter(searchText));
                }
            }

            private void initFeaturedOnlyFilter(se.devscout.server.api.activityfilter.AndFilter filter, ActivityFilterFactory mFilterFactory) {
                CheckBox featuredOnlyCheckbox = (CheckBox) searchView.findViewById(R.id.searchFeaturedOnly);
                if (featuredOnlyCheckbox.isChecked()) {
                    filter.getFilters().add(mFilterFactory.createIsFeaturedFilter());
                }
            }

            private void initFavouritesOnlyFilter(se.devscout.server.api.activityfilter.AndFilter filter, ActivityFilterFactory mFilterFactory) throws ActivityFilterFactoryException {
                CheckBox searchFavouritesOnlyCheckbox = (CheckBox) searchView.findViewById(R.id.searchFavouritesOnly);
                if (searchFavouritesOnlyCheckbox.isChecked()) {
                    filter.getFilters().add(mFilterFactory.createIsUserFavouriteFilter(null));
                }
            }

            private void addAgeFilter(se.devscout.server.api.activityfilter.OrFilter ageFilter, int toggleButtonResId, AgeGroup ageGroup, ActivityFilterFactory mFilterFactory) {
                CheckBox checkBox = (CheckBox) searchView.findViewById(toggleButtonResId);
                if (checkBox.isChecked()) {
                    ageFilter.getFilters().add(mFilterFactory.createAgeRangeFilter(ageGroup.getScoutAgeRange()));
                }
            }
        });
        return searchView;
    }

    private Spinner initTimeDropDown(View searchView) {
        final Spinner spinner = (Spinner) searchView.findViewById(R.id.searchTimeSpinner);
        ArrayAdapter<TimeRange> adapter = new ArrayAdapter<TimeRange>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                mTimeRanges);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private void initAdditionalCheckboxLabel(final View searchView, int labelId, final int checkBoxId) {
        View view = searchView.findViewById(labelId);
        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) searchView.findViewById(checkBoxId);
                checkBox.toggle();
            }
        });
//        view.setSelected(getPreferences().getBoolean("" + checkBoxId, false));
    }

    public static SearchFragment create() {
        return new SearchFragment();
    }
}

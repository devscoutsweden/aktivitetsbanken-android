package se.devscout.android.controller.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import se.devscout.android.AgeGroup;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SearchResultActivity;
import se.devscout.android.model.CategoryBean;
import se.devscout.android.model.IntegerRange;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.android.util.http.UnauthorizedException;
import se.devscout.server.api.ActivityFilterFactory;
import se.devscout.server.api.ActivityFilterFactoryException;
import se.devscout.server.api.model.Category;

import java.util.List;

public class SearchFragment extends ActivityBankFragment {

    private TimeRange[] mTimeRanges;
    private TimeRange anyTimeRange;
    private Category anyCategory;
    private int mInitialCategorySpinnerSelection;

    private static class TimeRange extends IntegerRange {
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
        editor.putInt("searchCategorySpinner", ((Spinner) view.findViewById(R.id.searchCategorySpinner)).getSelectedItemPosition());
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
        mInitialCategorySpinnerSelection = getPreferences().getInt("searchCategorySpinner", 0);
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
        final View searchView = inflater.inflate(R.layout.search, container, false);

        anyTimeRange = new TimeRange(0, Integer.MAX_VALUE, getString(R.string.searchTimeOptionAny));
        mTimeRanges = new TimeRange[]{
                anyTimeRange,
                new TimeRange(0, 5, getString(R.string.searchTimeOption5min)),
                new TimeRange(0, 15, getString(R.string.searchTimeOption15min)),
                new TimeRange(10, 35, getString(R.string.searchTimeOption15to30min)),
                new TimeRange(25, 65, getString(R.string.searchTimeOption30to60min)),
                new TimeRange(50, 140, getString(R.string.searchTimeOption1to2hours)),
                new TimeRange(120, Integer.MAX_VALUE, getString(R.string.searchTimeOptionMoreThan2hours))};

        final Spinner spinner = initTimeDropDown(searchView);

        anyCategory = new CategoryBean(null, getString(R.string.searchCategoryOptionAny), 0L, 0L, 0L, null);
        final Spinner categoryDropDown = initCategoryDropDown(searchView);

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

                    initCategory(filter, mFilterFactory);

                    if (filter.getFilters().size() == 0) {
                        Toast.makeText(getActivity(), R.string.searchNoFiltersMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(SearchResultActivity.createIntent(getActivity(), filter, getString(R.string.searchResultTitle)));
                    }
                } catch (ActivityFilterFactoryException e) {
//                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    LogUtil.e(SearchFragment.class.getName(), "Could not create activity filter.", e);
                }
            }

            private void initCategory(se.devscout.server.api.activityfilter.AndFilter filter, ActivityFilterFactory mFilterFactory) {
                Category selectedCategory = (Category) categoryDropDown.getSelectedItem();
                if (selectedCategory != anyCategory) {
                    filter.getFilters().add(mFilterFactory.createCategoryFilter(selectedCategory.getGroup(), selectedCategory.getName(), selectedCategory.getServerId()));
                }
            }

            private void initTimeRange(se.devscout.server.api.activityfilter.AndFilter filter, ActivityFilterFactory mFilterFactory) {
                TimeRange selectedTimeRange = (TimeRange) spinner.getSelectedItem();
                if (selectedTimeRange != anyTimeRange) {
                    // A time range is selected and it is not the "any-option".
                    se.devscout.server.api.activityfilter.TimeRangeFilter filter1 = mFilterFactory.createTimeRangeFilter(selectedTimeRange);
                    filter.getFilters().add(filter1);
                }
            }

            private void initAgeFilter(se.devscout.server.api.activityfilter.AndFilter filter, ActivityFilterFactory mFilterFactory) {
                IntegerRange range = new IntegerRange(Integer.MAX_VALUE, Integer.MAX_VALUE);
                addAgeFilter(range, R.id.searchAgeAdventurer, AgeGroup.ADVENTURER, mFilterFactory);
                addAgeFilter(range, R.id.searchAgeChallenger, AgeGroup.CHALLENGER, mFilterFactory);
                addAgeFilter(range, R.id.searchAgeDiscoverer, AgeGroup.DISCOVERER, mFilterFactory);
                addAgeFilter(range, R.id.searchAgeRover, AgeGroup.ROVER, mFilterFactory);
                addAgeFilter(range, R.id.searchAgeTracker, AgeGroup.TRACKER, mFilterFactory);

                if (range.getMax() != Integer.MAX_VALUE || range.getMin() != Integer.MAX_VALUE) {
                    filter.getFilters().add(mFilterFactory.createAgeRangeFilter(range));
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
                    filter.getFilters().add(mFilterFactory.createIsUserFavouriteFilter(PreferencesUtil.getInstance(getActivity()).getCurrentUser()));
                }
            }

            private void addAgeFilter(IntegerRange ageRange, int toggleButtonResId, AgeGroup ageGroup, ActivityFilterFactory mFilterFactory) {
                CheckBox checkBox = (CheckBox) searchView.findViewById(toggleButtonResId);
                if (checkBox.isChecked()) {
                    if (ageRange.getMin() > ageGroup.getScoutAgeRange().getMin() || ageRange.getMin() == Integer.MAX_VALUE) {
                        ageRange.setMin(ageGroup.getScoutAgeRange().getMin());
                    }
                    if (ageRange.getMax() < ageGroup.getScoutAgeRange().getMax() || ageRange.getMax() == Integer.MAX_VALUE) {
                        ageRange.setMax(ageGroup.getScoutAgeRange().getMax());
                    }
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

    private Spinner initCategoryDropDown(View searchView) {
        final Spinner spinner = (Spinner) searchView.findViewById(R.id.searchCategorySpinner);
        AsyncTask<Void, Void, Category[]> task = new AsyncTask<Void, Void, Category[]>() {

            @Override
            protected Category[] doInBackground(Void... voids) {
                try {
                    List<? extends Category> res = ActivityBankFactory.getInstance(getActivity()).readCategories();
                    Category[] categories1 = new Category[res.size() + 1];
                    categories1[0] = anyCategory;
                    for (int i = 0; i < res.size(); i++) {
                        Category category = res.get(i);
                        categories1[i + 1] = category;
                    }
                    return categories1;
                } catch (UnauthorizedException e) {
                    LogUtil.i(SearchFragment.class.getName(), "Could not complete search due to authorization problem.", e);
                    return new Category[]{anyCategory};
                }
            }

            @Override
            protected void onPostExecute(Category[] categories) {
                ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(
                        getActivity(),
                        android.R.layout.simple_spinner_item,
                        categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(Math.min(mInitialCategorySpinnerSelection, categories.length - 1));
                spinner.setVisibility(View.VISIBLE);
            }
        };
        task.execute();

        spinner.setVisibility(View.GONE);
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

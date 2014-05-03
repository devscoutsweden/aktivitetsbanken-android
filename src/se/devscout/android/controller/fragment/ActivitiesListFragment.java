package se.devscout.android.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.activity.ActivitiesActivity;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.android.util.ActivityUtil;
import se.devscout.android.view.AgeGroupView;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.Range;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActivitiesListFragment extends NonBlockingSearchResultFragment<Activity> {
    private Sorter mSortOrder;
    private ActivityFilter mFilter;

    public ActivitiesListFragment() {
    }

    public ActivitiesListFragment(ActivityFilter filter, Sorter sortOrder) {
        mFilter = filter;
        mSortOrder = sortOrder;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        List<ObjectIdentifierPojo> keys = new ArrayList<ObjectIdentifierPojo>();
        for (int i = 0; i < getListAdapter().getCount(); i++) {
            Activity activity = getListAdapter().getItem(i);
            keys.add(new ObjectIdentifierPojo(activity.getId()));
        }
        startActivity(ActivitiesActivity.createIntent(getActivity(), keys, position));
    }

    public static enum Sorter implements Comparator<Activity> {
        NAME(new Comparator<Activity>() {
            @Override
            public int compare(Activity localActivity, Activity localActivity2) {
                return ActivityUtil.getLatestActivityRevision(localActivity).getName().compareTo(ActivityUtil.getLatestActivityRevision(localActivity2).getName());
            }
        }),
        PARTICIPANT_COUNT(new RangeComparator() {
            @Override
            protected Range<Integer> getRange(Activity activity) {
                return ActivityUtil.getLatestActivityRevision(activity).getParticipants();
            }
        }),
        AGES(new RangeComparator() {
            @Override
            protected Range<Integer> getRange(Activity activity) {
                return ActivityUtil.getLatestActivityRevision(activity).getAges();
            }
        }),
        TIME(new RangeComparator() {
            @Override
            protected Range<Integer> getRange(Activity activity) {
                return ActivityUtil.getLatestActivityRevision(activity).getTimeActivity();
            }
        });

        private final Comparator<Activity> mComparator;

        Sorter(Comparator<Activity> comparator) {
            mComparator = comparator;
        }
        @Override
        public int compare(Activity activity, Activity activity2) {
            return mComparator.compare(activity, activity2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*
             * Restore fields from saved state, for example after the device has been rotated.
             */
            mSortOrder = (Sorter) savedInstanceState.getSerializable("mSortOrder");
            Log.d(ActivitiesListFragment.class.getName(), "State (e.g. search result) has been restored.");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected List<Activity> doSearch() {
        return (List<Activity>) ActivityBankFactory.getInstance(getActivity()).find(mFilter);
    }

    public void onSearchDone(List<Activity> result) {
        Log.d(ActivitiesListFragment.class.getName(), "Search task has completed. " + result.size() + " were returned.");

        if (getActivity() != null) {
            setSortOrder(mSortOrder);
        }
    }

    public void setSortOrder(Sorter sortOrder) {
        mSortOrder = sortOrder;
        if (mSortOrder != null) {
            getListAdapter().sort(mSortOrder);
        }
    }

    public Sorter getSortOrder() {
        return mSortOrder;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        Log.d(ActivitiesListFragment.class.getName(), "Saving state");
        outState.putSerializable("mSortOrder", mSortOrder);
        Log.d(ActivitiesListFragment.class.getName(), "State saved");
    }

    protected ArrayAdapter<Activity> createAdapter(List<Activity> result) {
        return new ActivitiesListAdapter(getActivity(), result, getActivity().getLayoutInflater());
    }

    @Override
    protected Activity getResultObjectFromId(ObjectIdentifierPojo identifier) {
        return ActivityBankFactory.getInstance(getActivity()).readFull(identifier);
    }

    public static ActivitiesListFragment create(List<ActivityKey> activities, Sorter defaultSortOrder) {
        ActivitiesListFragment fragment = new ActivitiesListFragment();
        ArrayList<ObjectIdentifierPojo> sortedList = new ArrayList<ObjectIdentifierPojo>();
        for (ActivityKey key : activities) {
            sortedList.add(new ObjectIdentifierPojo(key.getId()));
        }
        fragment.mSortOrder = defaultSortOrder;
        return fragment;
    }

    public static ActivitiesListFragment create(ActivityFilter filter, Sorter defaultSortOrder) {
        ActivitiesListFragment fragment = new ActivitiesListFragment();
        fragment.mFilter = filter;
        fragment.mSortOrder = defaultSortOrder;
        return fragment;
    }

    private static class ActivitiesListAdapter extends ArrayAdapter<Activity> {
        private LayoutInflater layoutInflater;

        public ActivitiesListAdapter(Context context, List<Activity> activities, LayoutInflater layoutInflater) {
            super(context, android.R.layout.simple_list_item_1, activities);
            this.layoutInflater = layoutInflater;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.activities_list_item, parent, false);
            }
            Activity activity = getItem(position);

            initTitle(convertView, activity);

            initDescription(convertView, activity);

            initTime(convertView, activity);

            initAgeGroups(convertView, activity);

            initPeople(convertView, activity);

            return convertView;
        }

        private void initAgeGroups(View convertView, Activity activity) {
            AgeGroupView view = (AgeGroupView) convertView.findViewById(R.id.activitiesListItemAgeGroups);
            Range<Integer> ages = ActivityUtil.getLatestActivityRevision(activity).getAges();
            boolean show = ages != null && ages.toString().length() > 0;
            if (show) {
                view.setMaxAge(ages.getMax());
                view.setMinAge(ages.getMin());
            }
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        private void initTitle(View convertView, Activity activity) {
            TextView titleTextView = (TextView) convertView.findViewById(R.id.activitiesListItemTitle);
            titleTextView.setText(ActivityUtil.getLatestActivityRevision(activity).getName());
        }

        private void initPeople(View convertView, Activity activity) {
            TextView participantsTextView = (TextView) convertView.findViewById(R.id.activitiesListItemParticipants);
            Range<Integer> participants = ActivityUtil.getLatestActivityRevision(activity).getParticipants();
            boolean show = participants != null && participants.toString().length() > 0;
            if (show) {
                participantsTextView.setText(getContext().getResources().getString(R.string.activitiesListItemParticipants, participants.toString()));
            }
            participantsTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        private void initTime(View convertView, Activity activity) {
            Range<Integer> timeActivity = ActivityUtil.getLatestActivityRevision(activity).getTimeActivity();
            TextView timeTextView = (TextView) convertView.findViewById(R.id.activitiesListItemTime);
            boolean show = timeActivity != null && timeActivity.toString().length() > 0;
            if (show) {
                timeTextView.setText(getContext().getResources().getString(R.string.activitiesListItemTime, timeActivity.toString()));
            }
            timeTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        private void initDescription(View convertView, Activity activity) {
            TextView descriptionTextView = (TextView) convertView.findViewById(R.id.activitiesListItemSubtitle);
            String description = ActivityUtil.getLatestActivityRevision(activity).getDescription();
            if (description != null) {
                descriptionTextView.setText(description.substring(0, Math.min(100, description.length())));
            }
            descriptionTextView.setVisibility(description != null ? View.VISIBLE : View.INVISIBLE);
        }
    }

}

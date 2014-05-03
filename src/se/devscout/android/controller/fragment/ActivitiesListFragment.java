package se.devscout.android.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import se.devscout.android.R;
import se.devscout.android.controller.activity.ActivitiesActivity;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.model.repo.SQLiteActivityRepo;
import se.devscout.android.util.ActivityUtil;
import se.devscout.android.view.AgeGroupView;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.Range;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActivitiesListFragment extends Fragment implements AdapterView.OnItemClickListener {
    protected ArrayList<ObjectIdentifierPojo> mActivities;
    private Sorter mSortOrder;
    private ListView mList;
    private FrameLayout mProgressView;
    private ActivityFilter mFilter;

    public ActivitiesListFragment() {
    }

    public ActivitiesListFragment(ActivityFilter filter, Sorter sortOrder) {
        mFilter = filter;
        mSortOrder = sortOrder;
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
        final View view = inflater.inflate(R.layout.search_result, container, false);

        mProgressView = (FrameLayout) view.findViewById(R.id.searchResultProgress);

        mList = (ListView) view.findViewById(R.id.searchResultList);
        mList.setVisibility(View.INVISIBLE);
        mList.setOnItemClickListener(this);

        if (savedInstanceState != null) {
                /*
                 * Restore fields from saved state, for example after the device has been rotated.
                 */
            mActivities = (ArrayList<ObjectIdentifierPojo>) savedInstanceState.getSerializable("mActivities");
            mSortOrder = (Sorter) savedInstanceState.getSerializable("mSortOrder");
            Log.d(ActivitiesListFragment.class.getName(), "State (e.g. search result) has been restored.");
        }

        if (mActivities != null) {
            // Result exits
            Log.d(ActivitiesListFragment.class.getName(), "Result exists. Display it.");
            showResult();
        } else {
            // Start search in separate thread
            Log.d(ActivitiesListFragment.class.getName(), "Result has not been returned/cached. Starting search task in new thread.");

            // TODO Use some kind of factory for accessing/creating the ActivityBank instead of forcing SQLiteActivityRepo?
            SearchActivitiesTask searchTask = new SearchActivitiesTask(SQLiteActivityRepo.getInstance(getActivity()), mFilter) {
                @Override
                protected void onPostExecute(List<? extends Activity> activities) {
                    Log.d(ActivitiesListFragment.class.getName(), "Search task has completed. " + activities.size() + " were returned.");
                    ArrayList<ObjectIdentifierPojo> sortedList = new ArrayList<ObjectIdentifierPojo>();
                    for (ActivityKey key : activities) {
                        sortedList.add(new ObjectIdentifierPojo(key.getId()));
                    }
                    mActivities = sortedList;

                    showResult();
                }
            };
            searchTask.execute();
        }

        return view;
    }

    private void showResult() {
        if (getActivity() != null) {
            mList.setVisibility(View.VISIBLE);
            mList.setAdapter(createAdapter());
            setSortOrder(mSortOrder);

            mProgressView.setVisibility(View.GONE);
            Log.d(ActivitiesListFragment.class.getName(), "Progress view has been hidden and list view has been shown.");
        } else {
            Log.d(ActivitiesListFragment.class.getName(), "No activity available. Result cannot be shown.");
        }
    }

    public ArrayAdapter<Activity> getListAdapter() {
        return (ArrayAdapter<Activity>) mList.getAdapter();
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
        outState.putSerializable("mActivities", mActivities);
        outState.putSerializable("mSortOrder", mSortOrder);
        Log.d(ActivitiesListFragment.class.getName(), "State saved");
    }

    protected ArrayAdapter<Activity> createAdapter() {
        return new ActivitiesListAdapter(getActivity(), getActivities(), getActivity().getLayoutInflater());
    }

    protected ArrayList<Activity> getActivities() {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        for (ObjectIdentifierPojo activity : mActivities) {
            //TODO: Save complete Activity objects in mActivities instead of only the keys? Performance gain or performance loss?
            activities.add(SQLiteActivityRepo.getInstance(getActivity()).read(activity));
        }
        return activities;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        List<ObjectIdentifierPojo> keys = new ArrayList<ObjectIdentifierPojo>();
        for (int i = 0; i < getListAdapter().getCount(); i++) {
            Activity activity = getListAdapter().getItem(i);
            keys.add(new ObjectIdentifierPojo(activity.getId()));
        }
        startActivity(ActivitiesActivity.createIntent(getActivity(), keys, position))
        ;
    }

    public static ActivitiesListFragment create(List<ActivityKey> activities, Sorter defaultSortOrder) {
        ActivitiesListFragment fragment = new ActivitiesListFragment();
        ArrayList<ObjectIdentifierPojo> sortedList = new ArrayList<ObjectIdentifierPojo>();
        for (ActivityKey key : activities) {
            sortedList.add(new ObjectIdentifierPojo(key.getId()));
        }
        fragment.mActivities = sortedList;
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

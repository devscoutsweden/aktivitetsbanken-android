package se.devscout.android.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.activity.ActivityActivity;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.model.repo.SQLiteActivityRepo;
import se.devscout.android.view.AgeGroupView;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.ActivityRevision;
import se.devscout.server.api.model.Range;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesListFragment extends ListFragment {
    protected ArrayList<ObjectIdentifierPojo> mActivities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            if (savedInstanceState != null) {
                /*
                 * Restore fields from saved state, for example after the device has been rotated.
                 */
                mActivities = (ArrayList<ObjectIdentifierPojo>) savedInstanceState.getSerializable("mActivities");
            }
            setListAdapter(createAdapter());
        } catch (RuntimeException e) {
            Log.e(getClass().getName(), "Error during onCreate", e);
            throw e;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Store fields into saved state, for example when the activity is destroyed after the device has been rotated.
         */
        outState.putSerializable("mActivities", mActivities);
    }

    protected ArrayAdapter<Activity> createAdapter() {
        return new ActivitiesListAdapter(getActivity(), getActivities(), getActivity().getLayoutInflater());
    }

    protected ArrayList<Activity> getActivities() {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        for (ObjectIdentifierPojo activity : mActivities) {
            activities.add(SQLiteActivityRepo.getInstance(getActivity()).read(activity));
        }
        return activities;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(ActivityActivity.createIntent(getActivity(), mActivities.get(position)));
    }

    public static ActivitiesListFragment create(List<ActivityKey> activities) {
        ActivitiesListFragment fragment = new ActivitiesListFragment();
        ArrayList<ObjectIdentifierPojo> sortedList = new ArrayList<ObjectIdentifierPojo>();
        for (ActivityKey key : activities) {
            sortedList.add(new ObjectIdentifierPojo(key.getId()));
        }
/*
        Collections.sort(sortedList, new Comparator<Activity>() {
            @Override
            public int compare(Activity activity, Activity activity2) {
                return activity != null ? getLatestActivityRevision(activity).getName().compareTo(getLatestActivityRevision(activity).getName()) : 0;
            }
        });
*/
        fragment.mActivities = sortedList;
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
            Range<Integer> ages = getLatestActivityRevision(activity).getAges();
            if (ages != null) {
                view.setMaxAge(ages.getMax());
                view.setMinAge(ages.getMin());
            }
            view.setVisibility(view != null ? View.VISIBLE : View.GONE);
        }

        private void initTitle(View convertView, Activity activity) {
            TextView titleTextView = (TextView) convertView.findViewById(R.id.activitiesListItemTitle);
            titleTextView.setText(getLatestActivityRevision(activity).getName());
        }

        private void initPeople(View convertView, Activity activity) {
            TextView participantsTextView = (TextView) convertView.findViewById(R.id.activitiesListItemParticipants);
            Range<Integer> participants = getLatestActivityRevision(activity).getParticipants();
            if (participants != null) {
                participantsTextView.setText(getContext().getResources().getString(R.string.activitiesListItemParticipants, participants.toString()));
            }
            participantsTextView.setVisibility(participantsTextView != null ? View.VISIBLE : View.GONE);
        }

        private void initTime(View convertView, Activity activity) {
            Range<Integer> timeActivity = getLatestActivityRevision(activity).getTimeActivity();
            TextView timeTextView = (TextView) convertView.findViewById(R.id.activitiesListItemTime);
            if (timeActivity != null) {
                timeTextView.setText(getContext().getResources().getString(R.string.activitiesListItemTime, timeActivity.toString()));
            }
            timeTextView.setVisibility(timeActivity != null ? View.VISIBLE : View.GONE);
        }

        private void initDescription(View convertView, Activity activity) {
            TextView descriptionTextView = (TextView) convertView.findViewById(R.id.activitiesListItemSubtitle);
            String description = getLatestActivityRevision(activity).getDescription();
            if (description != null) {
                descriptionTextView.setText(description.substring(0, Math.min(100, description.length())));
            }
            descriptionTextView.setVisibility(description != null ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private static ActivityRevision getLatestActivityRevision(Activity activity) {
        return activity.getRevisions().get(activity.getRevisions().size() - 1);
    }
}

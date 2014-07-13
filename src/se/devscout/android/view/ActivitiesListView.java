package se.devscout.android.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.activity.ActivitiesActivity;
import se.devscout.android.controller.fragment.RangeComparator;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.model.SearchHistoryPropertiesPojo;
import se.devscout.android.model.repo.SearchHistoryDataPojo;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.Range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ActivitiesListView extends NonBlockingSearchView<ActivitiesListItem> {
    private Sorter mSortOrder;
    private ActivityFilter mFilter;

    public ActivitiesListView(Context context, int emptyMessageTextId, int emptyHeaderTextId, ActivityFilter filter, Sorter sortOrder, boolean isListContentHeight) {
        super(context, emptyMessageTextId, emptyHeaderTextId, isListContentHeight);
        mFilter = filter;
        mSortOrder = sortOrder;
    }

    @Override
    protected void onItemClick(View view, int position) {
        List<ObjectIdentifierPojo> keys = new ArrayList<ObjectIdentifierPojo>();
        for (ActivitiesListItem activity : getItems()) {
            keys.add(new ObjectIdentifierPojo(activity.getId()));
        }
        getContext().startActivity(ActivitiesActivity.createIntent(getContext(), keys, position));
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putSerializable("mFilter", mFilter);
        bundle.putSerializable("mSortOrder", mSortOrder);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mFilter = (ActivityFilter) bundle.getSerializable("mFilter");
            mSortOrder = (Sorter) bundle.getSerializable("mSortOrder");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public SearchTask createSearchTask() {
        return new ActivitiesSearchTask();
    }

    @Override
    public void setResult(List<ActivitiesListItem> result) {
        if (mSortOrder != null) {
            Collections.sort(result, mSortOrder);
        }
        super.setResult(result);
    }

    public void setSortOrder(Sorter sortOrder) {
        mSortOrder = sortOrder;
        if (mSortOrder != null) {
            sort(mSortOrder);
        }
    }

    protected ArrayAdapter<ActivitiesListItem> createAdapter(List<ActivitiesListItem> result) {
        return new ActivitiesListAdapter(getContext(), result, LayoutInflater.from(getContext()));
    }

    public static enum Sorter implements Comparator<ActivitiesListItem> {
        NAME(new Comparator<ActivitiesListItem>() {
            @Override
            public int compare(ActivitiesListItem localActivity, ActivitiesListItem localActivity2) {
                return localActivity.getName().compareTo(localActivity2.getName());
            }
        }),
        PARTICIPANT_COUNT(new RangeComparator() {
            @Override
            protected Range<Integer> getRange(ActivitiesListItem activity) {
                return activity.getParticipants();
            }
        }),
        AGES(new RangeComparator() {
            @Override
            protected Range<Integer> getRange(ActivitiesListItem activity) {
                return activity.getAges();
            }
        }),
        TIME(new RangeComparator() {
            @Override
            protected Range<Integer> getRange(ActivitiesListItem activity) {
                return activity.getTimeActivity();
            }
        });

        private final Comparator<ActivitiesListItem> mComparator;

        Sorter(Comparator<ActivitiesListItem> comparator) {
            mComparator = comparator;
        }
        @Override
        public int compare(ActivitiesListItem activity, ActivitiesListItem activity2) {
            return mComparator.compare(activity, activity2);
        }

    }


    private static class ActivitiesListAdapter extends ArrayAdapter<ActivitiesListItem> {
        private LayoutInflater layoutInflater;

        public ActivitiesListAdapter(Context context, List<ActivitiesListItem> activities, LayoutInflater layoutInflater) {
            super(context, android.R.layout.simple_list_item_1, activities);
            this.layoutInflater = layoutInflater;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.activities_list_item, parent, false);
            }
            ActivitiesListItem activity = getItem(position);

            initTitle(convertView, activity);

            initDescription(convertView, activity);

            initTime(convertView, activity);

            initAgeGroups(convertView, activity);

            initPeople(convertView, activity);

            return convertView;
        }

        private void initAgeGroups(View convertView, ActivitiesListItem activity) {
            AgeGroupView view = (AgeGroupView) convertView.findViewById(R.id.activitiesListItemAgeGroups);
            Range<Integer> ages = activity.getAges();
            boolean show = ages != null && ages.toString().length() > 0;
            if (show) {
                view.setMaxAge(ages.getMax());
                view.setMinAge(ages.getMin());
            }
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        private void initTitle(View convertView, ActivitiesListItem activity) {
            TextView titleTextView = (TextView) convertView.findViewById(R.id.activitiesListItemTitle);
            titleTextView.setText(activity.getName());
        }

        private void initPeople(View convertView, ActivitiesListItem activity) {
            TextView participantsTextView = (TextView) convertView.findViewById(R.id.activitiesListItemParticipants);
            Range<Integer> participants = activity.getParticipants();
            boolean show = participants != null && participants.toString().length() > 0;
            if (show) {
                participantsTextView.setText(getContext().getResources().getString(R.string.activitiesListItemParticipants, participants.toString()));
            }
            participantsTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        private void initTime(View convertView, ActivitiesListItem activity) {
            Range<Integer> timeActivity = activity.getTimeActivity();
            TextView timeTextView = (TextView) convertView.findViewById(R.id.activitiesListItemTime);
            boolean show = timeActivity != null && timeActivity.toString().length() > 0;
            if (show) {
                timeTextView.setText(getContext().getResources().getString(R.string.activitiesListItemTime, timeActivity.toString()));
            }
            timeTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        private void initDescription(View convertView, ActivitiesListItem activity) {
            TextView descriptionTextView = (TextView) convertView.findViewById(R.id.activitiesListItemSubtitle);
            String description = activity.getDescription();
            if (description != null) {
                descriptionTextView.setText(description.substring(0, Math.min(100, description.length())));
            }
            descriptionTextView.setVisibility(description != null ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public class ActivitiesSearchTask extends SearchTask {

        @Override
        protected List<ActivitiesListItem> doSearch() {
            ActivityBank activityBank = ActivityBankFactory.getInstance(getContext());
            List<Activity> activities = (List<Activity>) activityBank.find(mFilter);
            final SearchHistoryDataPojo searchHistoryDataPojo = new SearchHistoryDataPojo(mFilter);
            activityBank.createSearchHistory(new SearchHistoryPropertiesPojo(null, searchHistoryDataPojo));
            List<ActivitiesListItem> items = new ArrayList<ActivitiesListItem>();
            for (Activity activity : activities) {
                items.add(new ActivitiesListItem(activity));
            }
            return items;
        }
    }
}

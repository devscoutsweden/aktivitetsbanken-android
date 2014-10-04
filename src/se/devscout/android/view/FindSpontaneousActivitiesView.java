package se.devscout.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import se.devscout.android.AgeGroup;
import se.devscout.android.R;
import se.devscout.android.model.IntegerRange;
import se.devscout.android.util.ActivityBankFactory;
import se.devscout.server.api.ActivityFilterFactory;
import se.devscout.server.api.activityfilter.AndFilter;

import java.io.Serializable;

public class FindSpontaneousActivitiesView<T extends Serializable> extends LinearLayout {
    public FindSpontaneousActivitiesView(Context context, boolean isListContentHeight) {
        super(context);
        init(context, isListContentHeight);
    }

    public FindSpontaneousActivitiesView(Context context, AttributeSet attrs, boolean isListContentHeight) {
        super(context, attrs);
        init(context, isListContentHeight);
    }

    public FindSpontaneousActivitiesView(Context context, AttributeSet attrs, int defStyle, boolean isListContentHeight) {
        super(context, attrs, defStyle);
        init(context, isListContentHeight);
    }

    private void init(Context context, boolean isListContentHeight) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.find_spontaneous_activity, this, true);

        ViewGroup container = (ViewGroup) findViewById(R.id.spontaneousContainer);
        initAdditionalCheckboxLabel(R.id.searchAgeAdventurerLogo, AgeGroup.ADVENTURER, container);
        initAdditionalCheckboxLabel(R.id.searchAgeChallengerLogo, AgeGroup.CHALLENGER, container);
        initAdditionalCheckboxLabel(R.id.searchAgeDiscovererLogo, AgeGroup.DISCOVERER, container);
        initAdditionalCheckboxLabel(R.id.searchAgeRoverLogo, AgeGroup.ROVER, container);
        initAdditionalCheckboxLabel(R.id.searchAgeTrackerLogo, AgeGroup.TRACKER, container);

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, isListContentHeight ? ViewGroup.LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT));
    }

    private void initAdditionalCheckboxLabel(int buttonId, final AgeGroup ageGroup, final ViewGroup container) {
        ImageView button = (ImageView) findViewById(buttonId);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityFilterFactory filterFactory = ActivityBankFactory.getInstance(getContext()).getFilterFactory();
                AndFilter filter = filterFactory.createAndFilter(
                        filterFactory.createAgeRangeFilter(ageGroup.getScoutAgeRange()),
                        filterFactory.createTimeRangeFilter(new IntegerRange(0, 15)),
                        filterFactory.createRandomActivitiesFilter(5)
                );
                View oldListView = container.findViewById(1010);
                if (oldListView != null) {
                    container.removeView(oldListView);
                }

//                Button button1 = new Button(getContext());
//                button1.setText("Hej");
//                container.addView(button1, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                ActivitiesListView listView = new ActivitiesListView(getContext(), R.string.noSpontaneousActivitiesFoundMessage, R.string.noSpontaneousActivitiesFoundTitle, filter, ActivitiesListView.Sorter.NAME, true);
                container.addView(listView);
                listView.setId(1010);
                listView.setVisibility(VISIBLE);
                listView.runSearchTaskInNewThread();
            }
        });
    }

/*
    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putSerializable("mResult", mResult);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mResult = (ArrayList<T>) bundle.getSerializable("mResult");
            state = bundle.getParcelable("instanceState");

            setResult(mResult);
        }
        super.onRestoreInstanceState(state);
    }
*/

}

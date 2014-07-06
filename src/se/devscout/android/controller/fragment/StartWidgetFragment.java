package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import se.devscout.android.R;
import se.devscout.android.view.ActivitiesListView;
import se.devscout.android.view.WidgetView;
import se.devscout.android.view.widget.*;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

import java.util.ArrayList;
import java.util.List;

public class StartWidgetFragment extends ActivityBankFragment implements ActivityBankListener {

    private boolean mRefreshResultOnResume = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("mRefreshResultOnResume", mRefreshResultOnResume);
        super.onSaveInstanceState(outState);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onDestroyView() {
        getActivityBank().removeListener(this);
        super.onDestroyView();
    }

    @Override
    public void onSearchHistoryItemAdded(SearchHistory searchHistory) {
    }

    @Override
    public void onFavouriteChange(ActivityKey activityKey, UserKey userKey, boolean isFavouriteNow) {
        synchronized (this) {
            mRefreshResultOnResume = true;
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        final LinearLayout ll = (LinearLayout) getView().findViewById(R.id.start_widgets_container);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View view = ll.getChildAt(i);

            if (view instanceof ActivitiesListView) {
                ActivitiesListView activitiesListView = (ActivitiesListView) view;
                if (mRefreshResultOnResume || !activitiesListView.isResultPresent()) {
                    activitiesListView.runSearchTaskInNewThread();
                }
            }

            if (view instanceof FragmentListener) {
                FragmentListener widget = (FragmentListener) view;
                widget.onFragmentResume(mRefreshResultOnResume);
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRefreshResultOnResume = savedInstanceState.getBoolean("mRefreshResultOnResume");
        }
        getActivityBank().addListener(this);

        final List<WidgetSpecification> mFragmentCreator = new ArrayList<WidgetSpecification>();
        mFragmentCreator.add(new WelcomeMessageWidgetSpecification());
        mFragmentCreator.add(new SearchWidgetSpecification());
        mFragmentCreator.add(new SpontaneousActivityWidgetSpecification());
        mFragmentCreator.add(new CategoriesWidgetSpecification());
        mFragmentCreator.add(new FeaturedWidgetSpecification());
        mFragmentCreator.add(new FavouritesWidgetSpecification());
        final View view = inflater.inflate(R.layout.start, container, false);
        final LinearLayout ll = (LinearLayout) view.findViewById(R.id.start_widgets_container);

        int id = 12345;
        for (WidgetSpecification widgetSpec : mFragmentCreator) {
            WidgetView widget = new WidgetView(getActivity(), widgetSpec.getTitleResId());
            widget.setId(id++);
            final View[] views = widgetSpec.getViews(inflater, widget, savedInstanceState, this);
            for (View view1 : views) {
                view1.setId(id++);
                widget.setContentView(view1);
            }

            ll.addView(widget);
        }

        return view;
    }

    public static StartWidgetFragment create() {
        StartWidgetFragment fragment = new StartWidgetFragment();
        return fragment;
    }
}

package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import se.devscout.android.R;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.android.view.ActivitiesListView;
import se.devscout.android.view.WidgetView;
import se.devscout.android.view.widget.FragmentListener;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

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

        final View view = inflater.inflate(R.layout.start, container, false);
        final LinearLayout ll = (LinearLayout) view.findViewById(R.id.start_widgets_container);

        int id = 12345;
        List<String> widgetIds = PreferencesUtil.getStringList(getPreferences(), "homeWidgets", null);
        for (AbstractActivitiesFinderComponentFactory finder : AbstractActivitiesFinderComponentFactory.getActivitiesFinders()) {
            if (finder.isWidgetCreator()) {
//                WidgetSpecification widgetSpec = finder.asWidgetSpecification();
                if ((widgetIds != null && widgetIds.contains(finder.getId())) || finder.isDefaultWidget()) {
                    WidgetView widget = new WidgetView(getActivity(), finder.isWidgetTitleImportant() ? finder.getTitleResId() : 0);
                    widget.setId(id++);
                    final View view1 = finder.createView(inflater, widget, savedInstanceState, this);
                    view1.setId(id++);
                    widget.setContentView(view1);

                    ll.addView(widget);
                }
            }
        }
        return view;
    }

    public static StartWidgetFragment create() {
        StartWidgetFragment fragment = new StartWidgetFragment();
        return fragment;
    }
}

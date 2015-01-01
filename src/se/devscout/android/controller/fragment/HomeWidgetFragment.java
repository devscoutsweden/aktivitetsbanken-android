package se.devscout.android.controller.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import se.devscout.android.R;
import se.devscout.android.util.DialogUtil;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.android.view.ActivitiesListView;
import se.devscout.android.view.WidgetView;
import se.devscout.android.view.widget.ComponentFactoryRepo;
import se.devscout.android.view.widget.FragmentListener;
import se.devscout.android.view.widget.WidgetComponentFactory;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

import java.util.*;

//TODO: It might be cleaner to create an AbstractActivityBankListener instead of implementing ActivityBankListener
public class HomeWidgetFragment extends ActivityBankFragment implements ActivityBankListener {

    private static final String PREFS_KEY_WIDGET_IDS = "homeWidgets";
    private static final List<String> DEFAULT_WIDGETS = Arrays.asList(
            ComponentFactoryRepo.WELCOME_MESSAGE,
            ComponentFactoryRepo.EXTENDED_SEARCH_ACTIVITIES,
            ComponentFactoryRepo.AUTHENTICATION,
            ComponentFactoryRepo.FEATURED_ACTIVITIES,
            ComponentFactoryRepo.CRASH_REPORTER);

    private boolean mRefreshResultOnResume = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("mRefreshResultOnResume", mRefreshResultOnResume);
        super.onSaveInstanceState(outState);
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
    public void onAsyncException(Exception e) {
    }

    @Override
    public void onResume() {
        super.onResume();

        final LinearLayout ll = (LinearLayout) getView().findViewById(R.id.home_widgets_container);
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

        final View view = inflater.inflate(R.layout.home, container, false);

        loadSelectedWidgets(inflater, view);

        return view;
    }

    private Dialog createWidgetSelectionDialog(final View view, final LayoutInflater inflater) {
        final Map<String, WidgetComponentFactory> allWidgets = getWidgetFactories(false);
        Map<String, WidgetComponentFactory> selectedWidgets = getWidgetFactories(true);

        LinkedHashMap<String, Boolean> options = new LinkedHashMap<String, Boolean>();

        for (Map.Entry<String, WidgetComponentFactory> entry : allWidgets.entrySet()) {
            String title = entry.getKey();
            boolean selected = selectedWidgets.keySet().contains(title);
            options.put(title, selected);
        }

        return DialogUtil.createMultiChoiceDialog(options, getActivity(), new DialogUtil.OnMultiChoiceDialogDone() {
            @Override
            public void onPositiveButtonClick(DialogInterface dialogInterface, List<String> selectedOptions) {
                ArrayList<String> values = getSelectedIds(selectedOptions);

                saveSelection(values);

                loadSelectedWidgets(inflater, view);
            }

            private ArrayList<String> getSelectedIds(List<String> selectedOptions) {
                ArrayList<String> values = new ArrayList<String>();
                for (String factory : selectedOptions) {
                    values.add(allWidgets.get(factory).getId());
                }
                return values;
            }

            private void saveSelection(ArrayList<String> values) {
                SharedPreferences.Editor editor = getPreferences().edit();
                PreferencesUtil.putStringList(PREFS_KEY_WIDGET_IDS, values, editor);
                editor.commit();
            }
        });
    }

    private void loadSelectedWidgets(final LayoutInflater inflater, final View view) {
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.home_widgets_container);
        int id = 12345;
        ll.removeAllViews();
        Map<String, WidgetComponentFactory> widgets = getWidgetFactories(true);
        for (WidgetComponentFactory finder : widgets.values()) {
            WidgetView widget = new WidgetView(getActivity(), finder.isWidgetTitleImportant() ? finder.getTitleResId() : 0);
            widget.setId(id++);
            final View view1 = finder.createView(inflater, widget, this);
            view1.setId(id++);
            widget.setContentView(view1);

            ll.addView(widget);
        }

        Button selectWidgetsButton = new Button(getActivity());
        selectWidgetsButton.setText(R.string.startSelectWidgets);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        selectWidgetsButton.setLayoutParams(params);
        selectWidgetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View buttonView) {
                Dialog dialog = createWidgetSelectionDialog(view, inflater);
                dialog.show();
            }
        });
        ll.addView(selectWidgetsButton);
    }

    private Map<String, WidgetComponentFactory> getWidgetFactories(boolean onlySelected) {
        List<String> widgetIds = PreferencesUtil.getStringList(getPreferences(), PREFS_KEY_WIDGET_IDS, DEFAULT_WIDGETS);
        Map<String, WidgetComponentFactory> factories = getWidgetFactories(onlySelected ? widgetIds : null);
        if (factories.isEmpty() && !widgetIds.isEmpty()) {
            return getWidgetFactories(DEFAULT_WIDGETS);
        }
        return factories;
    }

    private Map<String, WidgetComponentFactory> getWidgetFactories(List<String> widgetIds) {
        final Map<String, WidgetComponentFactory> allWidgets = new LinkedHashMap<String, WidgetComponentFactory>();
        for (WidgetComponentFactory finder : ComponentFactoryRepo.getInstance(getActivity()).getWidgetFactories()) {
            if (widgetIds == null || widgetIds.contains(finder.getId())) {
                allWidgets.put(getString(finder.getTitleResId()), finder);
            }
        }
        return allWidgets;
    }

    public static HomeWidgetFragment create() {
        HomeWidgetFragment fragment = new HomeWidgetFragment();
        return fragment;
    }

}

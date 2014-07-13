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
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.android.view.ActivitiesListView;
import se.devscout.android.view.WidgetView;
import se.devscout.android.view.widget.FragmentListener;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StartWidgetFragment extends ActivityBankFragment implements ActivityBankListener {

    private static final String PREFS_KEY_WIDGET_IDS = "homeWidgets";

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

        loadSelectedWidgets(inflater, view);

        return view;
    }

    private Dialog createWidgetSelectionDialog(final View view, final LayoutInflater inflater) {
        final Map<String, AbstractActivitiesFinderComponentFactory> allWidgets = getAllWidgets(false);
        Map<String, AbstractActivitiesFinderComponentFactory> selectedWidgets = getAllWidgets(true);

        LinkedHashMap<String, Boolean> options = new LinkedHashMap<String, Boolean>();

        for (Map.Entry<String, AbstractActivitiesFinderComponentFactory> entry : allWidgets.entrySet()) {
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
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.start_widgets_container);
        int id = 12345;
        ll.removeAllViews();
        for (AbstractActivitiesFinderComponentFactory finder : getAllWidgets(true).values()) {
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

    private Map<String, AbstractActivitiesFinderComponentFactory> getAllWidgets(boolean onlySelected) {
        final Map<String, AbstractActivitiesFinderComponentFactory> allWidgets = new LinkedHashMap<String, AbstractActivitiesFinderComponentFactory>();
        List<String> widgetIds = PreferencesUtil.getStringList(getPreferences(), PREFS_KEY_WIDGET_IDS, null);
        for (AbstractActivitiesFinderComponentFactory finder : AbstractActivitiesFinderComponentFactory.getActivitiesFinders()) {
            if (finder.isWidgetCreator()) {
                if (!onlySelected || ((widgetIds != null && widgetIds.contains(finder.getId())) || (widgetIds == null && finder.isDefaultWidget()))) {
                    allWidgets.put(getString(finder.getTitleResId()), finder);
                }
            }
        }
        return allWidgets;
    }

    public static StartWidgetFragment create() {
        StartWidgetFragment fragment = new StartWidgetFragment();
        return fragment;
    }
}

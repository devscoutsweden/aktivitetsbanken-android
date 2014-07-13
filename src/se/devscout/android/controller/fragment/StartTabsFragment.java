package se.devscout.android.controller.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.*;
import se.devscout.android.R;
import se.devscout.android.util.DialogUtil;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StartTabsFragment extends TabsFragment {

    private static final String PREFS_KEY_TAB_IDS = "tabs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.start, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.startMenu:
                createWidgetSelectionDialog().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected StaticFragmentsPagerAdapter createPagerAdapter(FragmentManager fragmentManager) {
        StaticFragmentsPagerAdapter pagerAdapter = new StaticFragmentsPagerAdapter(fragmentManager, getActivity());


        List<String> widgetIds = PreferencesUtil.getStringList(getPreferences(), PREFS_KEY_TAB_IDS, null);
        for (AbstractActivitiesFinderComponentFactory finder : AbstractActivitiesFinderComponentFactory.getActivitiesFinders()) {
            if (finder.isFragmentCreator()) {
                if ((widgetIds != null && widgetIds.contains(finder.getId())) || (widgetIds == null && finder.isDefaultFragment())) {
                    pagerAdapter.addTab(finder.getTitleResId(), finder.getIconResId(), finder.createFragment());
                }
            }
        }
        return pagerAdapter;
    }

    private Dialog createWidgetSelectionDialog() {
        final Map<String, AbstractActivitiesFinderComponentFactory> allWidgets = getAllTabs(false);
        Map<String, AbstractActivitiesFinderComponentFactory> selectedWidgets = getAllTabs(true);

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
                List<String> currentValues = PreferencesUtil.getStringList(getPreferences(), PREFS_KEY_TAB_IDS, null);
                if (!values.equals(currentValues)) {
                    saveSelection(values);

                    restartActivity(); // It's easier to restart the activity than to implement dynamically loaded view pager fragments.
                }
            }

            private void restartActivity() {
                getActivity().finish();
                startActivity(getActivity().getIntent());
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
                PreferencesUtil.putStringList(PREFS_KEY_TAB_IDS, values, editor);
                editor.commit();
            }
        });
    }

    private Map<String, AbstractActivitiesFinderComponentFactory> getAllTabs(boolean onlySelected) {
        final Map<String, AbstractActivitiesFinderComponentFactory> allTabs = new LinkedHashMap<String, AbstractActivitiesFinderComponentFactory>();
        List<String> tabIds = PreferencesUtil.getStringList(getPreferences(), PREFS_KEY_TAB_IDS, null);
        for (AbstractActivitiesFinderComponentFactory finder : AbstractActivitiesFinderComponentFactory.getActivitiesFinders()) {
            if (finder.isFragmentCreator()) {
                if (!onlySelected || ((tabIds != null && tabIds.contains(finder.getId())) || (tabIds == null && finder.isDefaultFragment()))) {
                    allTabs.put(getString(finder.getTitleResId()), finder);
                }
            }
        }
        return allTabs;
    }
}

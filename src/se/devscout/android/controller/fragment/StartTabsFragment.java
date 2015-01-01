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
import se.devscout.android.view.widget.ComponentSpecificationFactory;
import se.devscout.android.view.widget.TabComponentFactory;

import java.util.*;

public class StartTabsFragment extends TabsFragment {

    private static final String PREFS_KEY_TAB_IDS = "tabs";
    private static final List<String> DEFAULT_TABS = Arrays.asList(
            ComponentSpecificationFactory.START,
            ComponentSpecificationFactory.ACTIVITIES_BY_CATEGORY,
            ComponentSpecificationFactory.SEARCH_ACTIVITIES,
            ComponentSpecificationFactory.FAVOURITE_ACTIVITIES);

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

        for (TabComponentFactory finder : getTabFactories(true).values()) {
            pagerAdapter.addTab(finder.getTitleResId(), finder.getIconResId(), finder.createFragment());
        }
        return pagerAdapter;
    }

    private Dialog createWidgetSelectionDialog() {
        final Map<String, TabComponentFactory> allWidgets = getTabFactories(false);
        Map<String, TabComponentFactory> selectedWidgets = getTabFactories(true);

        LinkedHashMap<String, Boolean> options = new LinkedHashMap<String, Boolean>();

        for (Map.Entry<String, TabComponentFactory> entry : allWidgets.entrySet()) {
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

    private Map<String, TabComponentFactory> getTabFactories(boolean onlySelected) {

        List<String> tabIds = PreferencesUtil.getStringList(getPreferences(), PREFS_KEY_TAB_IDS, DEFAULT_TABS);
        Map<String, TabComponentFactory> factories = getTabFactories(onlySelected ? tabIds : null);
        if (factories.isEmpty() && !tabIds.isEmpty()) {
            return getTabFactories(DEFAULT_TABS);
        }
        return factories;
    }

    private Map<String, TabComponentFactory> getTabFactories(List<String> tabIds) {
        final Map<String, TabComponentFactory> allTabs = new LinkedHashMap<String, TabComponentFactory>();
        for (TabComponentFactory factory : ComponentSpecificationFactory.getInstance(getActivity()).getTabFactories()) {
            if (tabIds == null || tabIds.contains(factory.getId())) {
                allTabs.put(getString(factory.getTitleResId()), factory);
            }
        }
        return allTabs;
    }
}

package se.devscout.android.controller.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.activity.SingleFragmentActivity;
import se.devscout.android.util.DialogUtil;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.PreferencesUtil;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.android.view.ActivitiesListView;
import se.devscout.android.view.WidgetView;
import se.devscout.android.view.widget.FragmentListener;
import se.devscout.server.api.ActivityBankListener;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.SearchHistory;
import se.devscout.server.api.model.UserKey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;

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

        view.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SingleFragmentActivity)getActivity()).signInWithGplus();
            }
        });

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
        Map<String, AbstractActivitiesFinderComponentFactory> widgets = getAllWidgets(true);
        final File[] crashReportFiles = LogUtil.getCrashReportFiles(getActivity());
        Arrays.sort(crashReportFiles, new Comparator<File>() {
            @Override
            public int compare(File file, File file2) {
                return (int) (file2.lastModified() - file.lastModified());
            }
        });
        if (crashReportFiles.length > 0) {
            final Map<String, File> map = new LinkedHashMap<String, File>();
            for (File crashReportFile : crashReportFiles) {
                map.put(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(crashReportFile.lastModified())), crashReportFile);
            }
            widgets.put(getString(R.string.crashReportWidgetTitle), new AbstractActivitiesFinderComponentFactory(R.drawable.ic_action_bad, R.string.app_name) {
                @Override
                public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {
                    Button button = new Button(getActivity());
                    button.setText(getString(R.string.crashReportButtonLabel));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                    /*
                    http://www.androidsnippets.com/start-email-activity-with-preset-data-via-intents
                     */
                        public void onClick(View view) {

                            final String[] items = map.keySet().toArray(new String[map.size()]);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(getString(R.string.crashReportChooseCrashToReport));
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                        /* Create the Intent */
                                    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                        /* Fill it with Data */
                                    emailIntent.setType("plain/text");
                                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"devscout@mikaelsvensson.info"});
                                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "App Crash Report " + items[i]);

                                    File crashReportFile = map.get(items[i]);

                                    String versionCode = "unknown version code";
                                    String versionName = "unknown version name";
                                    try {
                                        PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                                        versionCode = String.valueOf(packageInfo.versionCode);
                                        versionName = packageInfo.versionName;
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }

                                    String value = MessageFormat.format("" +
                                            "App version: {11} ({10})\n" +
                                            "Android release: {2}\n" +
                                            "Device: {3} {4} {6} {7}\n" +
                                            "\n" +
                                            "{9}",
                                            crashReportFile.getName(),
                                            crashReportFile.length(),
                                            Build.VERSION.RELEASE,
                                            Build.BRAND,
                                            Build.DEVICE,
                                            Build.DISPLAY,
                                            Build.MODEL,
                                            Build.PRODUCT,
                                            Build.USER,
                                            readFile(crashReportFile),
                                            versionCode,
                                            versionName);
                                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, value);

                        /* Send it off to the Activity-Chooser */
                                    getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                                }

                                private String readFile(File crashReportFile) {
                                    StringBuilder msg = new StringBuilder();
                                    FileReader reader = null;
                                    try {
                                        reader = new FileReader(crashReportFile);
                                        BufferedReader bufReader = new BufferedReader(reader);
                                        String line = null;
                                        while ((line = bufReader.readLine()) != null) {
                                            msg.append(line);
                                            msg.append('\n');
                                        }
                                    } catch (IOException e) {
                                        LogUtil.e(StartWidgetFragment.class.getName(), "Could not read crash report reader", e);
                                    } finally {
                                        if (reader != null) {
                                            try {
                                                reader.close();
                                            } catch (IOException e) {
                                                LogUtil.e(StartWidgetFragment.class.getName(), "Could not close crash report reader", e);
                                            }
                                        }
                                    }
                                    return msg.toString();
                                }
                            });
                            builder.show();
                        }
                    });
                    LinearLayout ll = new LinearLayout(getActivity());
                    ll.setOrientation(LinearLayout.VERTICAL);
                    TextView textView = new TextView(getActivity());
                    textView.setText(getString(R.string.crashReportHelp));
                    ll.addView(textView);
                    ll.addView(button);
                    return ll;
                }
            });
        }
        for (AbstractActivitiesFinderComponentFactory finder : widgets.values()) {
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

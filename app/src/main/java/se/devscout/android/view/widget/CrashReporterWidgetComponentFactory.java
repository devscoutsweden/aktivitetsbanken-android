package se.devscout.android.view.widget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.HomeWidgetFragment;
import se.devscout.android.util.LogUtil;
import se.devscout.android.util.PreferencesUtil;

public class CrashReporterWidgetComponentFactory extends AbstractComponentFactory implements WidgetComponentFactory {
    private final Map<String, File> mMap;

    public CrashReporterWidgetComponentFactory(File[] crashReportFiles, String name) {
        super(name, R.string.crashReportWidgetTitle, R.drawable.ic_action_bad);
        Arrays.sort(crashReportFiles, new Comparator<File>() {
            @Override
            public int compare(File file, File file2) {
                return (int) (file2.lastModified() - file.lastModified());
            }
        });
        final Map<String, File> map = new LinkedHashMap<String, File>();
        for (File crashReportFile : crashReportFiles) {
            map.put(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(crashReportFile.lastModified())), crashReportFile);
        }
        mMap = map;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, final ActivityBankFragment activityBankFragment) {
        Button button = new Button(activityBankFragment.getActivity());
        button.setText(activityBankFragment.getString(R.string.crashReportButtonLabel));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
        /*
        http://www.androidsnippets.com/start-email-activity-with-preset-data-via-intents
         */
            public void onClick(View view) {

                final String[] items = mMap.keySet().toArray(new String[mMap.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(activityBankFragment.getActivity());
                builder.setTitle(activityBankFragment.getString(R.string.crashReportChooseCrashToReport));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
            /* Create the Intent */
                        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

            /* Fill it with Data */
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{
                                PreferencesUtil.getErrorReportMailAddress(activityBankFragment.getActivity())
                        });
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Crash Report " + items[i]);

                        File crashReportFile = mMap.get(items[i]);

                        String versionCode = "unknown version code";
                        String versionName = "unknown version name";
                        try {
                            PackageInfo packageInfo = activityBankFragment.getActivity().getPackageManager().getPackageInfo(activityBankFragment.getActivity().getPackageName(), 0);
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
                        emailIntent.putExtra(Intent.EXTRA_TEXT, value);

            /* Send it off to the Activity-Chooser */
                        activityBankFragment.getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    }

                    private String readFile(File crashReportFile) {
                        StringBuilder msg = new StringBuilder();
                        FileReader reader = null;
                        try {
                            reader = new FileReader(crashReportFile);
                            BufferedReader bufReader = new BufferedReader(reader);
                            String line;
                            while ((line = bufReader.readLine()) != null) {
                                msg.append(line);
                                msg.append('\n');
                            }
                        } catch (IOException e) {
                            LogUtil.e(HomeWidgetFragment.class.getName(), "Could not read crash report reader", e);
                        } finally {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    LogUtil.e(HomeWidgetFragment.class.getName(), "Could not close crash report reader", e);
                                }
                            }
                        }
                        return msg.toString();
                    }
                });
                builder.show();
            }
        });
        final int margin = activityBankFragment.getResources().getDimensionPixelSize(R.dimen.sideMargin);

        final LinearLayout.LayoutParams buttonLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayout.setMargins(margin, 0, margin, margin);
        buttonLayout.gravity = Gravity.CENTER_HORIZONTAL;
        button.setLayoutParams(buttonLayout);

        TextView text = new TextView(activityBankFragment.getActivity());
        text.setText(activityBankFragment.getString(R.string.crashReportHelp));
        final LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayout.setMargins(margin, margin, margin, margin);
        text.setLayoutParams(textLayout);

        LinearLayout ll = new LinearLayout(activityBankFragment.getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(text);
        ll.addView(button);

        return ll;
    }
}

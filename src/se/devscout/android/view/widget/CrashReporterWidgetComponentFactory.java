package se.devscout.android.view.widget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.controller.fragment.StartWidgetFragment;
import se.devscout.android.util.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;

public class CrashReporterWidgetComponentFactory extends AbstractActivitiesFinderComponentFactory implements WidgetComponentFactory {
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
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"devscout@mikaelsvensson.info"});
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
        LinearLayout ll = new LinearLayout(activityBankFragment.getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        TextView textView = new TextView(activityBankFragment.getActivity());
        textView.setText(activityBankFragment.getString(R.string.crashReportHelp));
        ll.addView(textView);
        ll.addView(button);
        return ll;
    }
}

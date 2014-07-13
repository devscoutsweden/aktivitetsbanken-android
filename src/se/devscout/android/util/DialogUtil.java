package se.devscout.android.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import se.devscout.android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogUtil {
    public static interface OnMultiChoiceDialogDone {
        void onPositiveButtonClick(DialogInterface dialogInterface, List<String> selectedOptions);
    }

    public static Dialog createMultiChoiceDialog(Map<String, Boolean> choices, Context ctx, final OnMultiChoiceDialogDone onMultiChoiceDialogDone) {
        final String[] options = new String[choices.size()];
        boolean[] initiallySelectedOptions = new boolean[choices.size()];
        final List<String> currentlySelectedItems = new ArrayList<String>();
        int i = 0;
        for (Map.Entry<String, Boolean> entry : choices.entrySet()) {
            String title = entry.getKey();
            Boolean selected = entry.getValue();
            options[i] = title;
            initiallySelectedOptions[i] = selected;
            if (selected) {
                currentlySelectedItems.add(title);
            }
            i++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        return builder.setMultiChoiceItems(options, initiallySelectedOptions, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index, boolean checked) {
                if (checked) {
                    currentlySelectedItems.add(options[index]);
                } else {
                    currentlySelectedItems.remove(options[index]);
                }
            }
        }).setPositiveButton(R.string.button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onMultiChoiceDialogDone.onPositiveButtonClick(dialogInterface, currentlySelectedItems);
            }
        }).setCancelable(true).create();
    }
}

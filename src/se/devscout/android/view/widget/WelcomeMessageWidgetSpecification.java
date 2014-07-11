package se.devscout.android.view.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;

public class WelcomeMessageWidgetSpecification extends AbstractActivitiesFinderComponentFactory {

    public WelcomeMessageWidgetSpecification(int nameResId, int iconResId) {
        super(iconResId, nameResId, true, false);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {
        TextView textView = new TextView(container.getContext());
        textView.setText(R.string.startIntroText);
        return textView;
    }
}

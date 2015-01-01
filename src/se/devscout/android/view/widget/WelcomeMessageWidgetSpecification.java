package se.devscout.android.view.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;
import se.devscout.android.view.AbstractActivitiesFinderComponentFactory;
import se.devscout.android.view.WidgetComponentFactory;

public class WelcomeMessageWidgetSpecification extends AbstractActivitiesFinderComponentFactory implements WidgetComponentFactory {

    public WelcomeMessageWidgetSpecification(String name) {
        super(name, R.drawable.ic_drawer, R.string.welcomeWidgetTitle, true, false);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {
        TextView textView = new TextView(container.getContext());
        textView.setText(R.string.startIntroText);
        return textView;
    }
}

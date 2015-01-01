package se.devscout.android.view.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.ActivityBankFragment;

public class WelcomeMessageComponentFactory extends AbstractComponentFactory implements WidgetComponentFactory {

    public WelcomeMessageComponentFactory(String name) {
        super(name, R.string.welcomeWidgetTitle, R.drawable.ic_drawer, false);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {
        TextView textView = new TextView(container.getContext());
        textView.setText(R.string.startIntroText);
        return textView;
    }
}

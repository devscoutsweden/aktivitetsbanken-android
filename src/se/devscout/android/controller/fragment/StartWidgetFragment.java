package se.devscout.android.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import se.devscout.android.R;
import se.devscout.android.controller.activity.*;
import se.devscout.android.view.StartWidgetView;

import java.util.ArrayList;
import java.util.List;

public class StartWidgetFragment extends ActivityBankFragment {

    @Override
    public void onResume() {
        super.onResume();

        final LinearLayout ll = (LinearLayout) getView().findViewById(R.id.start_widgets_container);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View view = ll.getChildAt(i);
            if (view instanceof StartWidgetView) {
                StartWidgetView widget = (StartWidgetView) view;
                widget.onResume();
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final List<StartScreenWidget> mFragmentCreator = new ArrayList<StartScreenWidget>();
        mFragmentCreator.add(new WelcomeMessageWidget());
        mFragmentCreator.add(new SearchWidget());
        mFragmentCreator.add(new SpontaneousActivityWidget());
        mFragmentCreator.add(new FeaturedWidget());
        mFragmentCreator.add(new FavouritesWidget());
        final View view = inflater.inflate(R.layout.start, container, false);
        final LinearLayout ll = (LinearLayout) view.findViewById(R.id.start_widgets_container);

        int id = 12345;
        for (StartScreenWidget widgetSpec : mFragmentCreator) {
            StartWidgetView widget = new StartWidgetView(getActivity(), widgetSpec.getTitleResId());
            widget.setId(id++);
            final View[] views = widgetSpec.getViews(inflater, widget, savedInstanceState);
            for (View view1 : views) {
                view1.setId(id++);
                widget.setContentView(view1);
            }

            ll.addView(widget);
        }

        return view;
    }

    public static StartWidgetFragment create() {
        StartWidgetFragment fragment = new StartWidgetFragment();
        return fragment;
    }
}

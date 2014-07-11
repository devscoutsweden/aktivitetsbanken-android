package se.devscout.android.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.*;
import se.devscout.android.view.widget.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractActivitiesFinderComponentFactory implements Serializable {

    private static final Collection<AbstractActivitiesFinderComponentFactory> ACTIVITIES_FINDERS = new ArrayList<AbstractActivitiesFinderComponentFactory>();

    static {
        ACTIVITIES_FINDERS.add(new AbstractActivitiesFinderComponentFactory(R.string.startTabHome, R.drawable.ic_action_storage, true) {
            @Override
            public Fragment createFragment() {
                return StartWidgetFragment.create();
            }
        });
        ACTIVITIES_FINDERS.add(new AbstractActivitiesFinderComponentFactory(R.string.startTabAgeGroups, R.drawable.ic_action_cc_bcc, false) {
            @Override
            public Fragment createFragment() {
                return AgeGroupListFragment.create();
            }
        });
        ACTIVITIES_FINDERS.add(new WelcomeMessageWidgetSpecification(0, R.drawable.ic_drawer));
        ACTIVITIES_FINDERS.add(new SearchActivitiesFinderComponentFactory(R.string.startTabSearch, R.drawable.ic_drawer));
        ACTIVITIES_FINDERS.add(new SpontaneousActivitiesFinderComponentFactory(R.string.startTabSpontaneousActivity, R.drawable.ic_drawer));
        ACTIVITIES_FINDERS.add(new FavouriteActivitiesFinderComponentFactory(R.string.startTabFavourites, R.drawable.ic_action_important));
        ACTIVITIES_FINDERS.add(new FeaturedActivitiesFinderComponentFactory(R.string.startTabFeatured, R.drawable.ic_action_good));
        ACTIVITIES_FINDERS.add(new ByCategoryActivitiesFinderComponentFactory(R.string.startTabCategories, R.drawable.ic_action_labels));
        ACTIVITIES_FINDERS.add(new AbstractActivitiesFinderComponentFactory(R.string.startTabSearch, R.drawable.ic_action_search, true) {
            @Override
            public Fragment createFragment() {
                return SearchFragment.create();
            }
        });
        ACTIVITIES_FINDERS.add(new AbstractActivitiesFinderComponentFactory(R.string.drawer_search_history_header, 0, false) {
            @Override
            public Fragment createFragment() {
                return SearchHistoryListFragment.create();
            }
        });
        ACTIVITIES_FINDERS.add(new AbstractActivitiesFinderComponentFactory(R.string.startTabTracks, 0, false) {
            @Override
            public Fragment createFragment() {
                return CategoryTrackListFragment.create();
            }
        });
    }

    public static Collection<AbstractActivitiesFinderComponentFactory> getActivitiesFinders() {
        return Collections.unmodifiableCollection(ACTIVITIES_FINDERS);
    }

    public boolean isFragmentCreator() {
        return mFragmentCreator;
    }

    public boolean isWidgetCreator() {
        return mWidgetCreator;
    }

    public boolean isWidgetTitleImportant() {
        return mWidgetTitleImportant;
    }

    public boolean isDefaultWidget() {
        return mDefaultWidget;
    }

    public boolean isDefaultFragment() {
        return mDefaultFragment;
    }

    public String getId() {
        return this.getClass().getSimpleName() + "-" + mNameResId + "-" + mIconResId;
    }

    private int mNameResId;
    private int mIconResId;
    private boolean mFragmentCreator;
    private boolean mWidgetCreator;
    private boolean mWidgetTitleImportant;
    private boolean mDefaultWidget;
    private boolean mDefaultFragment;

    /**
     * Factory creating both widgets and tabs (fragments).
     *
     * @param iconResId
     * @param nameResId
     * @param defaultFragment
     * @param defaultWidget
     * @param widgetTitleImportant
     */
    protected AbstractActivitiesFinderComponentFactory(int iconResId, int nameResId, boolean defaultFragment, boolean defaultWidget, boolean widgetTitleImportant) {
        this(nameResId, iconResId, true, defaultFragment, true, defaultWidget, widgetTitleImportant);
    }

    /**
     * Widget-only factory.
     *
     * @param iconResId
     * @param nameResId
     * @param defaultWidget
     * @param widgetTitleImportant
     */
    protected AbstractActivitiesFinderComponentFactory(int iconResId, int nameResId, boolean defaultWidget, boolean widgetTitleImportant) {
        this(nameResId, iconResId, false, false, true, defaultWidget, widgetTitleImportant);
    }

    /**
     * Tab-only factory.
     *
     * @param nameResId
     * @param iconResId
     * @param defaultFragment
     */
    protected AbstractActivitiesFinderComponentFactory(int nameResId, int iconResId, boolean defaultFragment) {
        this(nameResId, iconResId, true, defaultFragment, false, false, false);
    }

    private AbstractActivitiesFinderComponentFactory(int nameResId, int iconResId, boolean fragmentCreator, boolean defaultFragment, boolean widgetCreator, boolean defaultWidget, boolean widgetTitleImportant) {
        mDefaultFragment = defaultFragment;
        mDefaultWidget = defaultWidget;
        mFragmentCreator = fragmentCreator;
        mIconResId = iconResId;
        mNameResId = nameResId;
        mWidgetCreator = widgetCreator;
        mWidgetTitleImportant = widgetTitleImportant;
    }

    protected AbstractActivitiesFinderComponentFactory(int iconResId, int nameResId) {
        mIconResId = iconResId;
        mNameResId = nameResId;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public int getTitleResId() {
        return mNameResId;
    }

    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, ActivityBankFragment activityBankFragment) {
        throw new UnsupportedOperationException("createView has not been implemented by " + getClass().getSimpleName() + ". Blame the developer.");
    }

    public Fragment createFragment() {
        throw new UnsupportedOperationException("createFragment has not been implemented by " + getClass().getSimpleName() + ". Blame the developer.");
    }
}

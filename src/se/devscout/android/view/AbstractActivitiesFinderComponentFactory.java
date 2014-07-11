package se.devscout.android.view;

import android.support.v4.app.Fragment;
import se.devscout.android.R;
import se.devscout.android.controller.activity.FragmentCreator;
import se.devscout.android.controller.fragment.*;
import se.devscout.android.view.widget.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractActivitiesFinderComponentFactory implements Serializable {

    private static final Collection<AbstractActivitiesFinderComponentFactory> ACTIVITIES_FINDERS = new ArrayList<AbstractActivitiesFinderComponentFactory>();

    public static Collection<AbstractActivitiesFinderComponentFactory> getActivitiesFinders() {
        return Collections.unmodifiableCollection(ACTIVITIES_FINDERS);
    }

    private static abstract class ActivitiesFinderComponentFactoryDefault extends AbstractActivitiesFinderComponentFactory implements FragmentCreator {
        private boolean mDefaultTab;

        private ActivitiesFinderComponentFactoryDefault(int nameResId, int iconResId, boolean defaultTab) {
            super(iconResId, nameResId);
            mDefaultTab = defaultTab;
        }

        @Override
        public boolean isDefaultTab() {
            return mDefaultTab;
        }
    }

    public String getId() {
        return this.getClass().getSimpleName() + "-" + mNameResId + "-" + mIconResId;
    }

    static {
        ACTIVITIES_FINDERS.add(new ActivitiesFinderComponentFactoryDefault(R.string.startTabHome, R.drawable.ic_action_storage, true) {
            @Override
            public Fragment createFragment() {
                return StartWidgetFragment.create();
            }
        });
        ACTIVITIES_FINDERS.add(new ActivitiesFinderComponentFactoryDefault(R.string.startTabAgeGroups, R.drawable.ic_action_cc_bcc, false) {
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
        ACTIVITIES_FINDERS.add(new ActivitiesFinderComponentFactoryDefault(R.string.startTabSearch, R.drawable.ic_action_search, true) {
            @Override
            public Fragment createFragment() {
                return SearchFragment.create();
            }
        });
        ACTIVITIES_FINDERS.add(new ActivitiesFinderComponentFactoryDefault(R.string.drawer_search_history_header, 0, false) {
            @Override
            public Fragment createFragment() {
                return SearchHistoryListFragment.create();
            }
        });
        ACTIVITIES_FINDERS.add(new ActivitiesFinderComponentFactoryDefault(R.string.startTabTracks, 0, false) {
            @Override
            public Fragment createFragment() {
                return CategoryTrackListFragment.create();
            }
        });
    }

    private int mNameResId;
    private int mIconResId;

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

    public boolean isStartTabCreator() {
        return this instanceof FragmentCreator;
    }

    public boolean isWidgetCreator() {
        return this instanceof WidgetSpecification;
    }

    public FragmentCreator asFragmentCreator() {
        if (isStartTabCreator()) {
            return (FragmentCreator) this;
        }
        throw new ClassCastException(getClass().getName() + " is not a fragment creator.");
    }

    public WidgetSpecification asWidgetSpecification() {
        if (isWidgetCreator()) {
            return (WidgetSpecification) this;
        }
        throw new ClassCastException(getClass().getName() + " is not a widget specification.");
    }
}

package se.devscout.android.view;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.devscout.android.controller.fragment.ActivityBankFragment;

import java.io.Serializable;

public abstract class AbstractActivitiesFinderComponentFactory implements Serializable {

//    private static final Collection<AbstractActivitiesFinderComponentFactory> ACTIVITIES_FINDERS = new ArrayList<AbstractActivitiesFinderComponentFactory>();

/*
    static {
        ACTIVITIES_FINDERS.add(new StartTabComponentFactory("Start"));
        ACTIVITIES_FINDERS.add(new ByAgeGroupComponentFactory("ActivitiesByAgeGroup"));
        ACTIVITIES_FINDERS.add(new WelcomeMessageWidgetSpecification("welcome"));
        ACTIVITIES_FINDERS.add(new LogInWidgetSpecification("authentication"));
        ACTIVITIES_FINDERS.add(new SearchActivitiesFinderComponentFactory("SearchActivities"));
        ACTIVITIES_FINDERS.add(new SpontaneousActivitiesFinderComponentFactory("SpontaneousActivities"));
        ACTIVITIES_FINDERS.add(new FavouriteActivitiesFinderComponentFactory("FavouriteActivities"));
        ACTIVITIES_FINDERS.add(new OverallFavouriteActivitiesFinderComponentFactory("OverallFavouriteActivities"));
        ACTIVITIES_FINDERS.add(new FeaturedActivitiesFinderComponentFactory("FeaturedActivities"));
        ACTIVITIES_FINDERS.add(new CategoryTrackActivitiesFinderComponentFactory("ActivitiesByCategoryTrack"));
        ACTIVITIES_FINDERS.add(new SearchTabActivitiesFinderComponentFactory("Search"));
        ACTIVITIES_FINDERS.add(new SearchHistoryComponentFactory("SearchHistory"));
        ACTIVITIES_FINDERS.add(new CategoryTrackActivitiesFinderComponentFactory("ActivitiesByCategoryTrack"));
    }
*/

    private String mUniqueName;

/*
    public static Collection<AbstractActivitiesFinderComponentFactory> getActivitiesFinders() {
        return Collections.unmodifiableCollection(ACTIVITIES_FINDERS);
    }
*/

    public boolean isFragmentCreator() {
        return mFragmentCreator;
    }

    public boolean isWidgetCreator() {
        return this instanceof WidgetComponentFactory;// mWidgetCreator;
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
        return mUniqueName;
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
     * @param uniqueName
     * @param iconResId
     * @param nameResId
     * @param defaultFragment
     * @param defaultWidget
     * @param widgetTitleImportant
     */
    protected AbstractActivitiesFinderComponentFactory(String uniqueName, int iconResId, int nameResId, boolean defaultFragment, boolean defaultWidget, boolean widgetTitleImportant) {
        this(uniqueName, nameResId, iconResId, true, defaultFragment, true, defaultWidget, widgetTitleImportant);
    }

    /**
     * Widget-only factory.
     *
     * @param iconResId
     * @param nameResId
     * @param defaultWidget
     * @param widgetTitleImportant
     */
    protected AbstractActivitiesFinderComponentFactory(String uniqueName, int iconResId, int nameResId, boolean defaultWidget, boolean widgetTitleImportant) {
        this(uniqueName, nameResId, iconResId, false, false, true, defaultWidget, widgetTitleImportant);
    }

    /**
     * Tab-only factory.
     *
     * @param nameResId
     * @param iconResId
     * @param defaultFragment
     */
    protected AbstractActivitiesFinderComponentFactory(String uniqueName, int nameResId, int iconResId, boolean defaultFragment) {
        this(uniqueName, nameResId, iconResId, true, defaultFragment, false, false, false);
    }

    private AbstractActivitiesFinderComponentFactory(String uniqueName, int nameResId, int iconResId, boolean fragmentCreator, boolean defaultFragment, boolean widgetCreator, boolean defaultWidget, boolean widgetTitleImportant) {
        mDefaultFragment = defaultFragment;
        mDefaultWidget = defaultWidget;
        mFragmentCreator = fragmentCreator;
        mUniqueName = uniqueName;
        mIconResId = iconResId;
        mNameResId = nameResId;
        mWidgetCreator = widgetCreator;
        mWidgetTitleImportant = widgetTitleImportant;
    }

    protected AbstractActivitiesFinderComponentFactory(String uniqueName, int iconResId, int nameResId) {
        mUniqueName = uniqueName;
        mIconResId = iconResId;
        mNameResId = nameResId;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public int getTitleResId() {
        return mNameResId;
    }

    public View createView(LayoutInflater inflater, ViewGroup container, ActivityBankFragment activityBankFragment) {
        throw new UnsupportedOperationException("createView has not been implemented by " + getClass().getSimpleName() + ". Blame the developer.");
    }

    public Fragment createFragment() {
        throw new UnsupportedOperationException("createFragment has not been implemented by " + getClass().getSimpleName() + ". Blame the developer.");
    }
}

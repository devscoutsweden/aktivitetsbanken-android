package se.devscout.android.view.widget;

import android.content.Context;
import se.devscout.android.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComponentSpecificationFactory {
    public static final String CRASH_REPORTER = "CrashReporter";
    public static final String WELCOME = "welcome";
    public static final String SPONTANEOUS_ACTIVITIES = "SpontaneousActivities";
    public static final String SEARCH_ACTIVITIES = "SearchActivities";
    public static final String OVERALL_FAVOURITE_ACTIVITIES = "OverallFavouriteActivities";
    public static final String AUTHENTICATION = "authentication";
    public static final String FEATURED_ACTIVITIES = "FeaturedActivities";
    public static final String FAVOURITE_ACTIVITIES = "FavouriteActivities";
    public static final String ACTIVITIES_BY_CATEGORY = "ActivitiesByCategory";
    public static final String START = "Start";
    public static final String ACTIVITIES_BY_AGE_GROUP = "ActivitiesByAgeGroup";
    public static final String ACTIVITIES_BY_CATEGORY_TRACK = "ActivitiesByCategoryTrack";
    public static final String SEARCH_HISTORY = "SearchHistory";
    public static final String SEARCH = "Search";
    private static ComponentSpecificationFactory factory = null;
    private final Context mContext;

    public static ComponentSpecificationFactory getInstance(Context context) {
        if (factory == null) {
            synchronized (ComponentSpecificationFactory.class) {
                if (factory == null) {
                    factory = new ComponentSpecificationFactory(context);
                }
            }
        }
        return factory;
    }

    private ComponentSpecificationFactory(Context context) {
        mContext = context;
    }

    public List<WidgetComponentFactory> getWidgetFactories() {

        ArrayList<WidgetComponentFactory> factories = new ArrayList<WidgetComponentFactory>();
        factories.add(new WelcomeMessageWidgetSpecification(WELCOME));
        factories.add(new LogInWidgetSpecification(AUTHENTICATION));
        factories.add(new SearchActivitiesFinderComponentFactory(SEARCH_ACTIVITIES));
        factories.add(new SpontaneousActivitiesFinderComponentFactory(SPONTANEOUS_ACTIVITIES));
        factories.add(new FeaturedActivitiesFinderComponentFactory(FEATURED_ACTIVITIES));
        factories.add(new FavouriteActivitiesFinderComponentFactory(FAVOURITE_ACTIVITIES));
        factories.add(new OverallFavouriteActivitiesFinderComponentFactory(OVERALL_FAVOURITE_ACTIVITIES));
        factories.add(new ByCategoryActivitiesFinderComponentFactory(ACTIVITIES_BY_CATEGORY));

        final File[] crashReportFiles = LogUtil.getCrashReportFiles(mContext);
        if (crashReportFiles.length > 0) {
            factories.add(new CrashReporterWidgetComponentFactory(crashReportFiles, CRASH_REPORTER));
        }

        return factories;
    }

    public List<TabComponentFactory> getTabFactories() {
        ArrayList<TabComponentFactory> factories = new ArrayList<TabComponentFactory>();
        factories.add(new StartTabComponentFactory(START));
        factories.add(new ByCategoryActivitiesFinderComponentFactory(ACTIVITIES_BY_CATEGORY));
        factories.add(new FeaturedActivitiesFinderComponentFactory(FEATURED_ACTIVITIES));
        factories.add(new FavouriteActivitiesFinderComponentFactory(FAVOURITE_ACTIVITIES));
        factories.add(new OverallFavouriteActivitiesFinderComponentFactory(OVERALL_FAVOURITE_ACTIVITIES));
        factories.add(new ByAgeGroupComponentFactory(ACTIVITIES_BY_AGE_GROUP));
        factories.add(new CategoryTrackActivitiesFinderComponentFactory(ACTIVITIES_BY_CATEGORY_TRACK));
        factories.add(new SearchTabActivitiesFinderComponentFactory(SEARCH));
        factories.add(new SearchHistoryComponentFactory(SEARCH_HISTORY));
        return factories;
    }

    public WidgetComponentFactory widgetComponentFactoryFromId(String id) {
        for (WidgetComponentFactory factory : getWidgetFactories()) {
            if (factory.getId().equals(id)) {
                return factory;
            }
        }
        return null;
    }

    public TabComponentFactory tabComponentFactoryFromId(String id) {
        for (TabComponentFactory factory : getTabFactories()) {
            if (factory.getId().equals(id)) {
                return factory;
            }
        }
        return null;
    }
}

package se.devscout.android.view.widget;

import android.content.Context;
import se.devscout.android.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComponentFactoryRepo {
    public static final String CRASH_REPORTER = "CrashReporter";
    public static final String WELCOME_MESSAGE = "WelcomeMessage";
    public static final String RECENT_ACTIVITIES = "RecentActivities";
    public static final String SPONTANEOUS_ACTIVITIES = "SpontaneousActivities";
    public static final String SIMPLE_SEARCH_ACTIVITIES = "SimpleSearchActivities";
    public static final String OVERALL_FAVOURITE_ACTIVITIES = "OverallFavouriteActivities";
    public static final String AUTHENTICATION = "Authentication";
    public static final String FEATURED_ACTIVITIES = "FeaturedActivities";
    public static final String FAVOURITE_ACTIVITIES = "FavouriteActivities";
    public static final String ACTIVITIES_BY_CATEGORY = "ActivitiesByCategory";
    public static final String HOME = "Home";
    public static final String ACTIVITIES_BY_AGE_GROUP = "ActivitiesByAgeGroup";
    public static final String ACTIVITIES_BY_CATEGORY_TRACK = "ActivitiesByCategoryTrack";
    public static final String SEARCH_HISTORY = "SearchHistory";
    public static final String EXTENDED_SEARCH_ACTIVITIES = "ExtendedSearchActivities";
    private static ComponentFactoryRepo factory = null;
    private final Context mContext;

    public static ComponentFactoryRepo getInstance(Context context) {
        if (factory == null) {
            synchronized (ComponentFactoryRepo.class) {
                if (factory == null) {
                    factory = new ComponentFactoryRepo(context);
                }
            }
        }
        return factory;
    }

    private ComponentFactoryRepo(Context context) {
        mContext = context;
    }

    public List<WidgetComponentFactory> getWidgetFactories() {

        ArrayList<WidgetComponentFactory> factories = new ArrayList<WidgetComponentFactory>();
        factories.add(new WelcomeMessageComponentFactory(WELCOME_MESSAGE));
        factories.add(new RecentActivitiesComponentFactory(RECENT_ACTIVITIES));
        factories.add(new AuthenticationComponentFactory(AUTHENTICATION));
        factories.add(new SimpleSearchActivitiesComponentFactory(SIMPLE_SEARCH_ACTIVITIES));
        factories.add(new SpontaneousActivitiesComponentFactory(SPONTANEOUS_ACTIVITIES));
        factories.add(new FeaturedActivitiesComponentFactory(FEATURED_ACTIVITIES));
        factories.add(new FavouriteActivitiesComponentFactory(FAVOURITE_ACTIVITIES));
        factories.add(new OverallFavouriteActivitiesComponentFactory(OVERALL_FAVOURITE_ACTIVITIES));
        factories.add(new ActivitiesByCategoryComponentFactory(ACTIVITIES_BY_CATEGORY));

        final File[] crashReportFiles = LogUtil.getCrashReportFiles(mContext);
        if (crashReportFiles.length > 0) {
            factories.add(new CrashReporterWidgetComponentFactory(crashReportFiles, CRASH_REPORTER));
        }

        return factories;
    }

    public List<TabComponentFactory> getTabFactories() {
        ArrayList<TabComponentFactory> factories = new ArrayList<TabComponentFactory>();
        factories.add(new HomeComponentFactory(HOME));
        factories.add(new ActivitiesByCategoryComponentFactory(ACTIVITIES_BY_CATEGORY));
        factories.add(new FeaturedActivitiesComponentFactory(FEATURED_ACTIVITIES));
        factories.add(new FavouriteActivitiesComponentFactory(FAVOURITE_ACTIVITIES));
        factories.add(new OverallFavouriteActivitiesComponentFactory(OVERALL_FAVOURITE_ACTIVITIES));
        factories.add(new ActivitiesByAgeGroupComponentFactory(ACTIVITIES_BY_AGE_GROUP));
        factories.add(new ActivitiesByCategoryTrackComponentFactory(ACTIVITIES_BY_CATEGORY_TRACK));
        factories.add(new ExtendedSearchActivitiesComponentFactory(EXTENDED_SEARCH_ACTIVITIES));
        factories.add(new SearchHistoryComponentFactory(SEARCH_HISTORY));
        return factories;
    }

}

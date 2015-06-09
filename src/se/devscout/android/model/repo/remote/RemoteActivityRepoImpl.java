package se.devscout.android.model.repo.remote;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import se.devscout.android.BuildConfig;
import se.devscout.android.R;
import se.devscout.android.controller.fragment.TitleActivityFilterVisitor;
import se.devscout.android.model.*;
import se.devscout.android.model.repo.sql.LocalObjectRefreshness;
import se.devscout.android.model.repo.sql.SQLiteActivityRepo;
import se.devscout.android.util.*;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.android.util.http.*;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.activityfilter.IsUserFavouriteFilter;
import se.devscout.server.api.model.*;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RemoteActivityRepoImpl extends SQLiteActivityRepo implements CredentialsManager.Listener {
    private static final String DEFAULT_REQUEST_BODY_ENCODING = "utf-8";
    private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HTTP_HEADER_X_ANDROID_APP_INSTALLATION_ID = "X-AndroidAppInstallationId";
    private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
    private static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String HTTP_HEADER_API_KEY = "X-ScoutAPI-APIKey";

    private static final String API_TOKEN_TYPE_API_KEY = "apikey";
    private static final String API_TOKEN_TYPE_GOOGLE = "google";

    private static final String HOST_PRODUCTION = "aktivitetsbanken.devscout.se";
    private static final int PORT_PRODUCTION = 80;

    private static final String HOST_TEST = "devscout.mikaelsvensson.info";
    private static final int PORT_TEST = 10081;

    private static final String HOST = BuildConfig.DEBUG ? HOST_TEST : HOST_PRODUCTION;
    private static final int PORT = BuildConfig.DEBUG ? PORT_TEST : PORT_PRODUCTION;
    private static final String SYSTEM_MESSAGE_KEY_API_HOST = "api:host";
    private static final String SYSTEM_MESSAGE_KEY_CONTACT_ERROR = "contact:error";

    private static RemoteActivityRepoImpl ourInstance;
    private final Context mContext;

    private List<ObjectIdentifierBean> mActivitiesWhichAreOld = new ArrayList<ObjectIdentifierBean>();
    private List<CategoryBean> mCachedCategories;
    //    private String authToken;
//    private String authType;
    //TODO: Use synchronized map since onAuthenticated is called from AsyncTask!?
    private HashMap<Long, Credentials> mCredentialsMap = new HashMap<Long, Credentials>();
    private int timeoutCounter;


    public static RemoteActivityRepoImpl getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = new RemoteActivityRepoImpl(ctx);
        }
        return ourInstance;
    }

    public RemoteActivityRepoImpl(Context ctx) {
        super(ctx);
        mContext = ctx;
        CredentialsManager.getInstance(mContext).addListener(this);
    }

    private String getRemoteHost() {
        String userSelectedHost = PreferencesUtil.getServerAddress(mContext);
        String host = userSelectedHost;
        if (TextUtils.isEmpty(host)) {
            List<String> hostNames = getSystemMessages(SYSTEM_MESSAGE_KEY_API_HOST);
            if (hostNames.size() > 0) {
                host = hostNames.get(0);
            } else {
                host = HOST + ":" + PORT;
            }
        }
        return host;
    }

    @Override
    public User readUser(UserKey key) {
        //TODO: implement/overload/fix this method. Low priority.
        return super.readUser(key);
    }

    @Override
    public Activity createActivity(ActivityProperties properties) {
        //TODO: implement/overload/fix this method. Low priority.
        return super.createActivity(properties);
    }

    @Override
    public Reference createReference(ActivityKey key, ReferenceProperties properties) {
        //TODO: this method probably does not have to be overloaded, provided references are automatically created by the server when activities are created/updated. Low priority.
        return super.createReference(fixActivityKey(key), properties);
    }

/*
    @Override
    public SearchHistory createSearchHistory(HistoryProperties<SearchHistoryData> properties, UserKey userKey) {
        //TODO: this method probably does not have to be overloaded, as long as search history should only be stored server-side as well. Low priority.
        return super.createSearchHistory(properties, userKey);
    }
*/

    @Override
    public ActivityHistory createActivityHistory(HistoryProperties<ActivityHistoryData> properties, UserKey userKey) {
        ActivityHistoryPropertiesBean fixedProperties = new ActivityHistoryPropertiesBean(userKey, new ActivityHistoryDataBean(fixActivityKey(new ObjectIdentifierBean(properties.getData().getId()))));
        return super.createActivityHistory(fixedProperties, userKey);
    }

    @Override
    public void deleteActivity(ActivityKey key) {
        //TODO: implement/overload/fix this method. Low priority.
        super.deleteActivity(fixActivityKey(key));
    }

    @Override
    public void deleteReference(ActivityKey key, ReferenceKey referenceKey) {
        //TODO: this method probably does not have to be overloaded, provided references are automatically created by the server when activities are created/updated. Low priority.
        super.deleteReference(fixActivityKey(key), referenceKey);
    }

    @Override
    public void deleteSearchHistory(int itemsToKeep) {
        //TODO: this method probably does not have to be overloaded, as long as search history should only be stored server-side as well.
        super.deleteSearchHistory(itemsToKeep);
    }

    @Override
    public Rating readRating(ActivityKey activityKey, UserKey userKey) {
        return super.readRating(fixActivityKey(activityKey), userKey);
    }

/*
    @Override
    public boolean createAnonymousAPIUser() {
        try {
            String uri = "http://" + getRemoteHost() + "/api/v1/" + "users";
            JSONObject body = new JSONObject();
            body.put("display_name", "android-app-" + InstallationProperties.getInstance(mContext).getId());
            JSONObject response = getJSONObject(uri, body, HttpMethod.POST);
            String apiKey = response.getString("api_key");
            if (apiKey != null && apiKey.length() > 0) {
                boolean success = setAnonymousUserAPIKey(apiKey, PreferencesUtil.getInstance(mContext).getCurrentUser());
                if (!success) {
                    LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Failed to set API key.");
                }
                return success;
            } else {
                LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Did not receive API key in server response.");
                return false;
            }
        } catch (JSONException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not create anonymous API user because of JSON problem.", e);
            return false;
        } catch (IOException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not create anonymous API user because of I/O problem.", e);
            return false;
        } catch (UnauthorizedException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not create anonymous API user because of authorization problem.", e);
            return false;
        } catch (UnhandledHttpResponseCodeException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not create anonymous API user because of an unhandled problem.", e);
            return false;
        }
    }
*/

/*
    @Override
    public void updateUser(UserKey key, UserProperties properties) throws UnauthorizedException {
        super.updateUser(key, properties);
        try {
            String uri = getURL("users/" + key.getId());

            JSONObject body = new JSONObject();
            body.put("display_name", properties.getDisplayName());
            body.put("email", properties.getEmailAddress());

            readUrlAsBytes(uri, body.toString(), HttpMethod.PUT);
        } catch (IOException | JSONException | UnhandledHttpResponseCodeException | UnauthorizedException e) {
            handleRemoteException(e, "Could not update user information because of an unhandled problem.");
        }
    }
*/

    @Override
    public URI getMediaItemURI(MediaProperties mediaProperties, int width, int height) {
        if (mediaProperties != null && width > 0 && height > 0) {
            try {
                return new URI(getURL("media_files/" + mediaProperties.getServerId() + "/file?size=" + Math.max(width, height)));
            } catch (URISyntaxException e) {
                LogUtil.e(RemoteActivityRepoImpl.class.getName(), "A very unexpected exception was thrown, but life (app!) will go on.", e);
                return super.getMediaItemURI(mediaProperties, width, height);
            }
        } else {
            return null;
        }
    }

//    @Override
//    public boolean isLoggedIn() {
//        return mCredentialsMap.containsKey(getCurrentUserId());
//    }

//    @Override
//    public void logOut() {
//        mCredentialsMap.remove(getCurrentUserId());
//        fireLoggedOut();
//    }

    @Override
    public void onAuthenticationStatusChange(CredentialsManager.State currentState) {
    }

    @Override
    public void onAuthenticated(IdentityProvider provider, String data, UserProperties userProperties) {
        switch (provider) {
            case GOOGLE:
                mCredentialsMap.put(
                        getCurrentUserId(),
                        new Credentials(data, API_TOKEN_TYPE_GOOGLE));

                if (userProperties != null) {
                    /*
                     * Updating the user profile will do three things:
                     *
                     * 1. it validates the Google id token (supplied in the
                     *    Authorization header from the mCredentialsMap object)
                     *
                     * 2. it creates a new user (and API key) if the Google id
                     *    token translates to a Google user id which has not
                     *    previously been encountered by the system
                     *
                     * 3. it sets the display name of said user.
                     *
                     * The server will return the user's API key in the
                     * response headers. The user, after the Google id token has
                     * been verified, will be created if not create before.
                     */
                    try {
                        updateUserProfile(userProperties);
                        synchronizeFavourites();
                        synchronizeRatings();
                        // todo: Update local database based on information from server (in case user has updated server-side profile data using other client)
                    } catch (UnauthorizedException e) {
                        LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not provide user information to server", e);
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("Does not support identity provider " + provider);
        }
    }

    private void synchronizeRatings() throws UnauthorizedException {
        // Get all ratings for current user from local database
        List<Rating> ratings = mDatabaseHelper.readRatings(getCurrentUser());
        ActivityKey[] ratedActivitiesKeys = new ActivityKey[ratings.size()];
        int i = 0;
        for (Rating rating : ratings) {
            ratedActivitiesKeys[i++] = fixActivityKey(new ObjectIdentifierBean(rating.getActivityId()));
        }

        // Build list of each locally rated activity's corresponding server id.
        ActivityList ratedActivities = mDatabaseHelper.readActivities(ratedActivitiesKeys);
        HashMap<Long, Long> serverIds = new HashMap<Long, Long>();
        for (Activity ratedActivity : ratedActivities) {
            serverIds.put(ratedActivity.getId(), ratedActivity.getServerId());
        }

        // Iterate over user's ratings in app
        for (Rating rating : ratings) {
            Long serverActivityId = serverIds.get(fixActivityKey(new ObjectIdentifierBean(rating.getActivityId())).getId());
            String uri = getURL("activities/" + serverActivityId + "/rating");
            try {
                switch (rating.getStatus()) {
                    case NO_CHANGE:
                        break;
                    case CHANGED:
                        // Update server-side rating and the set local status to NO_CHANGE.
                        readUrlAsBytes(uri, new JSONObject(Collections.singletonMap("rating", rating.getRating())).toString(), HttpMethod.POST);
                        mDatabaseHelper.setRating(new ObjectIdentifierBean(rating.getActivityId()), getCurrentUser(), new RatingPropertiesBean(rating.getRating(), RatingStatus.NO_CHANGE));
                        break;
                    case REMOVED:
                        // Rating was marked as removed (scheduled for deletion). Remove it from the server and then remove it from the app.
                        readUrlAsBytes(uri, null, HttpMethod.DELETE);
                        mDatabaseHelper.removeRating(new ObjectIdentifierBean(rating.getActivityId()), getCurrentUser());
                        break;
                }
            } catch (IOException | UnhandledHttpResponseCodeException | UnauthorizedException e) {
                handleRemoteException(e, "Could not update server-side rating for activity " + rating.getActivityId());
                fireServiceDegradation(mContext.getString(R.string.remote_could_not_save_ratings), e);
                return;
            }
        }
    }

    /**
     * 1. Get local ids of favourite activities stored in app database.
     * 2. Get server ids of favourite activities stored server-side.
     * 3. Read activities from server which are favourites but have not yet been cached/stored in app database.
     * 4. Make sure all server-side favourites are also marked as favourites in app database.
     * 5. Send complete list of favourites to server.
     */
    private void synchronizeFavourites() throws UnauthorizedException {

        //
        // Determine which server-side favourites have NOT been cached locally
        //
        List<ServerObjectIdentifierBean> serverFavouritesMissingInDatabase = new ArrayList<ServerObjectIdentifierBean>();
        try {
            String uri = getURL("favourites");
            JSONArray favouriteActivityKeysStoredRemotely = getJSONArray(uri, null);
            for (int i = 0; i < favouriteActivityKeysStoredRemotely.length(); i++) {
                long remoteId = favouriteActivityKeysStoredRemotely.getLong(i);
                long id = mDatabaseHelper.getLocalIdByServerId(new ActivityPropertiesBean(false, remoteId, 0, null));
                if (id == -1) {
                    serverFavouritesMissingInDatabase.add(new ServerObjectIdentifierBean(remoteId));
                }
            }
        } catch (IOException | JSONException | UnhandledHttpResponseCodeException | UnauthorizedException e) {
            handleRemoteException(e, "Could not retrieve user's list of favourites from server. Aborting synchronizeFavourites().");
            fireServiceDegradation(mContext.getString(R.string.remote_could_not_save_favourites), e);
            return;
        }

        //
        // Fetch/cache all missing activities. Then mark them as favourites in the local database.
        //

        if (!serverFavouritesMissingInDatabase.isEmpty()) {
            RemoteServerObjectIdentifiersFilter condition = new RemoteServerObjectIdentifiersFilter(serverFavouritesMissingInDatabase.toArray(new ServerObjectIdentifier[serverFavouritesMissingInDatabase.size()]));
            List<ActivityBean> missingActivities = findActivities(condition, null);
            for (ActivityBean missingActivity : missingActivities) {
                super.setFavourite(missingActivity, getCurrentUser());
            }
        }

        //
        // Send all favourites to server. At this point, this will be all the server-side favourites combined with all client-side favourites.
        //
        sendPutFavouritesRequest();
    }

    @Override
    public Boolean isFavourite(ActivityKey activityKey, UserKey userKey) {
        ActivityKey key = fixActivityKey(activityKey);
        return key != null ? super.isFavourite(key, userKey) : null;
    }

    @Override
    public UserProfileBean readUserProfile() {
        // Load user information from app database...
        UserProfileBean userProfile = super.readUserProfile();

        // ...and complement with information from the server (also use the name and e-mail reported by server instead of what is stored in app database).
        String uri = getURL("users/profile");
        try {
            JSONObject object = getJSONObject(uri, null, HttpMethod.GET);
            UserProfileBean mergedUserProfile = new UserProfileBean(
                    object.getString("display_name"),
                    userProfile.getAPIKey(),
                    userProfile.getId(),
                    userProfile.getServerId(),
                    userProfile.getServerRevisionId(),
                    object.getString("email"));

            // Build list of permissions
            mergedUserProfile.setRole(object.getString("role"));
            JSONArray rolePermissions = object.getJSONArray("role_permissions");
            String[] permissions = new String[rolePermissions.length()];
//            boolean isAllowedToUseTestAPI = "administrator".equalsIgnoreCase(mergedUserProfile.getRole());
            for (int i = 0; i < rolePermissions.length(); i++) {
                String role = rolePermissions.getString(i);
                permissions[i] = role;
//                isAllowedToUseTestAPI |= "administrator".equalsIgnoreCase(role);
            }
            mergedUserProfile.setRolePermissions(permissions);

//            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean("is_allowed_to_use_test_api", isAllowedToUseTestAPI);

            // Assume that the first key is good and valid.
            List<JSONObject> objects = getJSONObjectList(object, "keys");
            if (!objects.isEmpty()) {
                mergedUserProfile.setAPIKey(objects.get(0).getString("key"));
            }

            // todo: Update local database based on information from server (in case user has updated server-side profile data using other client)

            return mergedUserProfile;
        } catch (IOException | JSONException | UnauthorizedException | UnhandledHttpResponseCodeException e) {
            return userProfile;
        }
    }

    @Override
    public void updateUserProfile(UserProperties properties) throws UnauthorizedException {
        super.updateUserProfile(properties);
        try {
            String uri = getURL("users/profile");

            JSONObject body = new JSONObject();
            body.put("display_name", properties.getDisplayName());
            body.put("email", properties.getEmailAddress());

            readUrlAsBytes(uri, body.toString(), HttpMethod.PUT);
        } catch (IOException | JSONException | UnhandledHttpResponseCodeException | UnauthorizedException e) {
            handleRemoteException(e, "Could not update user profile because of an unhandled problem.");
        }
    }

    private ActivityKey fixActivityKey(ActivityKey activityKey) {
        if (activityKey.getId() < 0) {
            long id = mDatabaseHelper.getLocalIdByServerId(new ActivityPropertiesBean(false, -activityKey.getId(), 0, null));
            if (id != -1) {
                return new ObjectIdentifierBean(id);
            } else {
                LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not determine local id based on remote id " + id);
                return null;
            }
        } else {
            return activityKey;
        }
    }

    @Override
    public Category readCategoryFull(CategoryKey key) {
        //TODO: implement/overload/fix this method. High priority.
        return super.readCategoryFull(key);
    }

    @Override
    public ActivityList readActivities(ActivityKey... keys) throws UnauthorizedException {
        //TODO: implement/overload/fix this method. High priority.
        List<ServerObjectIdentifierBean> missing = new ArrayList<ServerObjectIdentifierBean>();
        List<ActivityKey> existing = new ArrayList<ActivityKey>();
        for (ActivityKey key : keys) {
            if (key.getId() < 0) {
                long id = mDatabaseHelper.getLocalIdByServerId(new ActivityPropertiesBean(false, -key.getId(), 0, null));
                boolean isUptodateInDatabase = !mActivitiesWhichAreOld.contains(new ObjectIdentifierBean(id));
                if (id != -1 && isUptodateInDatabase) {
                    LogUtil.i(RemoteActivityRepoImpl.class.getName(), "Activity with serverId = " + -key.getId() + " was initially not cached but is now.");
                    existing.add(new ObjectIdentifierBean(id));
                } else {
                    missing.add(new ServerObjectIdentifierBean(-key.getId()));
                }
            } else {
                existing.add(key);
            }
        }

        LogUtil.d(RemoteActivityRepoImpl.class.getName(), "readActivities: missing=" + TextUtils.join(",", missing) + " existing=" + TextUtils.join(",", existing));

        ActivityList result = new NegativeIdFixActivityList();

        ActivityList existingActivities = super.readActivities(existing.toArray(new ActivityKey[existing.size()]));
        result.addAll(existingActivities);

        if (!missing.isEmpty()) {
            LogUtil.i(RemoteActivityRepoImpl.class.getName(), "Requesting data for these server-only activities: " + TextUtils.join(",", missing));
            ServerObjectIdentifier[] identifiers = new ServerObjectIdentifier[missing.size()];
            RemoteServerObjectIdentifiersFilter condition = new RemoteServerObjectIdentifiersFilter(missing.toArray(identifiers));

            List<ActivityBean> missingActivities = findActivities(condition, null);
            result.addAll(missingActivities);
        }

        return result;
    }

    @Override
    public List<? extends Activity> readRelatedActivities(ActivityKey primaryActivity, List<? extends ActivityKey> forcedRelatedActivities) throws UnauthorizedException {
        return super.readRelatedActivities(
                /* Correct, if necessary, the activity key of the activity for which we want to fetch related activities. */
                fixActivityKey(primaryActivity),
                /* No need to fix the keys for the "forced related activities" since they will be fixed by readActivities, when called. */
                forcedRelatedActivities);
    }

    @Override
    public List<Reference> readReferences(ActivityKey activityKey) {
        //TODO: implement/overload/fix this method. High priority.
        ActivityKey key = fixActivityKey(activityKey);
        return key != null ? super.readReferences(key) : null;
    }

/*
    @Override
    public List<? extends SearchHistory> readSearchHistory(int limit, UserKey userKey) {
        return super.readSearchHistory(limit, userKey);
    }
*/

    @Override
    public void setFavourite(ActivityKey activityKey, UserKey userKey) throws UnauthorizedException {
        ActivityKey key = fixActivityKey(activityKey);
        if (key != null) {
            super.setFavourite(key, userKey);
            sendPutFavouritesRequest();
        }
    }

    @Override
    public void unsetFavourite(ActivityKey activityKey, UserKey userKey) throws UnauthorizedException {
        ActivityKey key = fixActivityKey(activityKey);
        if (key != null) {
            super.unsetFavourite(key, userKey);
            sendPutFavouritesRequest();
        }
    }

    private void sendPutFavouritesRequest() throws UnauthorizedException {
        try {
            List<ActivityBean> favourites = super.findActivity(getFilterFactory().createIsUserFavouriteFilter(getCurrentUser()));
//        Set<ActivityKey> favourites = mDatabaseHelper.getFavourites(PreferencesUtil.getInstance(mContext).getCurrentUser());
            JSONArray jsonArray = new JSONArray();
            for (ActivityBean key : favourites) {
                jsonArray.put(key.getServerId());
            }
            String uri = getURL("favourites");

            readUrlAsBytes(uri, new JSONObject(Collections.singletonMap("id", jsonArray)).toString(), HttpMethod.PUT);
        } catch (IOException | UnhandledHttpResponseCodeException | UnauthorizedException e) {
            handleRemoteException(e);
            fireServiceDegradation(mContext.getString(R.string.remote_could_not_save_favourites), e);
        }
    }

    @Override
    public ActivityProperties updateActivity(ActivityKey activityKey, ActivityProperties properties) {
        //TODO: implement/overload/fix this method. Low priority.
        ActivityKey key = fixActivityKey(activityKey);
        mActivitiesWhichAreOld.remove(new ObjectIdentifierBean(key.getId()));
        return key != null ? super.updateActivity(key, properties) : null;
    }

    @Override
    public List<ActivityBean> findActivity(ActivityFilter condition) throws UnauthorizedException {
        String[] attrNames = {
                "name",
                "featured",
                "age_max",
                "age_min",
                "participants_max",
                "participants_min",
                "time_max",
                "time_min",
                "media_files",
                /* Returning updated_at is important in order to detect server-side changes, which will trigger the app to, later on, update its cached data based on new data from the server. */
                "updated_at"};
        return findActivities(condition, attrNames);
    }

    private List<ActivityBean> findActivities(ActivityFilter condition, String[] attrNames) throws UnauthorizedException {

        if (condition instanceof IsUserFavouriteFilter) {
            LogUtil.d(RemoteActivityRepoImpl.class.getName(), "Never ask server for user's favourite activities. Assume that local database is up-to-date since last log-in.");
            return super.findActivity(condition);
        }

        URIBuilderActivityFilterVisitor visitor = new ApiV1Visitor(getURL("activities"));
        Uri uri = null;
        try {
            uri = condition.visit(visitor);
        } catch (UnsupportedOperationException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "App does not think API can handle the search query. Delegate search to database and pray that the database supports the query!");
            return super.findActivity(condition);
        }
        if (attrNames != null) {
            ArrayList attrs = new ArrayList(Arrays.asList(attrNames));
            attrs.add("favourite_count");
            attrs.add("ratings_average");
            uri = uri.buildUpon().appendQueryParameter("attrs", TextUtils.join(",", attrs)).build();
        }
        StopWatch stopWatch = new StopWatch("findActivity " + condition.visit(new TitleActivityFilterVisitor(mContext)));
        try {
            stopWatch.logEvent("Preparing request");
            JSONArray array = getJSONArray(uri.toString(), null);
            stopWatch.logEvent("Sending query to server and reading response");
            ArrayList<ActivityBean> result = new ArrayList<ActivityBean>();
            stopWatch.logEvent("Parsed JSON");
            for (JSONObject obj : getJSONArrayAsList(array)) {
                ServerActivityBean act = getActivityBean(obj);

                if (attrNames == null) {
                    processServerObject(act);
                } else {
                    LocalObjectRefreshness localFreshness = mDatabaseHelper.getLocalFreshness(act);
                    long localId = mDatabaseHelper.getLocalIdByServerId(act);
                    switch (localFreshness) {
                        case LOCAL_IS_UP_TO_DATE:
                            act.setId(localId);
                            break;
                        case LOCAL_IS_OLD:
                            act.setId(localId);
                            mActivitiesWhichAreOld.add(new ObjectIdentifierBean(localId));
                            //TODO: Document how negative numbers are used to indicate "activities which have not yet been stored locally"
                            act.setId(-act.getServerId());
                            break;
                        case LOCAL_IS_MISSING:
                            act.setId(-act.getServerId());
                            break;
                    }
                }

                result.add(act);
            }
            stopWatch.logEvent("Saving all returned data in local database");
            return result;
        } catch (IOException | JSONException | UnhandledHttpResponseCodeException | UnauthorizedException e) {
            handleRemoteException(e, "Could not search for activities");
            fireServiceDegradation(mContext.getString(R.string.remote_could_not_find_activities), e);
            return super.findActivity(condition);
        } finally {
            stopWatch.logEvent("The last things");
            LogUtil.d(RemoteActivityRepoImpl.class.getName(), stopWatch.getSummary());
        }
    }

    private void handleRemoteException(Exception e) throws UnauthorizedException {
        handleRemoteException(e, "Something went wrong when communicating with the server");
    }

    private void handleRemoteException(Exception e, String msg) throws UnauthorizedException {
        LogUtil.e(RemoteActivityRepoImpl.class.getName(), msg, e);
        if (e instanceof UnauthorizedException) {
            UnauthorizedException ue = (UnauthorizedException) e;
            if (ue.isAuthorizationHeaderProvided()) {
                throw ue;
            } else {
                // The server says that the user is not authorized and the app didn't even send any credentials. Maybe the user would have been permitted had he/she only been logged in!? At least we could try to search the app's local database -- maybe that will return some results.
            }
        }
    }

    private void processServerObject(ServerActivityBean act) {
        //TODO: refactor into separate method for updating database.
        LocalObjectRefreshness localFreshness = mDatabaseHelper.getLocalFreshness(act);
        switch (localFreshness) {
            case LOCAL_IS_MISSING:
                // Incoming data is a new (non-cached) activity. Add it to the local database.
                long id = mDatabaseHelper.createActivity(act);
                act.setId(id);
                break;
            case LOCAL_IS_OLD:
                // Incoming data is newer than cached data
                act.setId(mDatabaseHelper.getLocalIdByServerId(act));
                mDatabaseHelper.updateActivity(act, act);
                mActivitiesWhichAreOld.remove(new ObjectIdentifierBean(act.getId()));
                break;
            case LOCAL_IS_UP_TO_DATE:
                // No need to do anything
                act.setId(mDatabaseHelper.getLocalIdByServerId(act));
                break;
        }

        ObjectIdentifierBean activityKey = new ObjectIdentifierBean(act.getId());
        processServerObjectRating(activityKey, act);
    }

    private void processServerObjectRating(ObjectIdentifierBean localActivityKey, ServerActivityBean serverActivity) {
        Rating appRating = mDatabaseHelper.readRating(localActivityKey, getCurrentUser());
        Double serverRating = serverActivity.getMyRating();
        if (appRating == null) {
            // No rating stored locally.
            if (serverRating != null) {
                // Server-side rating exists. Save same rating locally.
                mDatabaseHelper.setRating(localActivityKey, getCurrentUser(), new RatingPropertiesBean(serverRating.intValue(), RatingStatus.NO_CHANGE));
            }
        } else {
            switch (appRating.getStatus()) {
                case CHANGED:
                    if (serverRating != null && appRating != null) {
                        if (serverRating.intValue() != appRating.getRating()) {
                            // TODO: Update server-side rating with app's rating
                        } else {
                            // Rating has been changed to the same value by both the app and some other client. Reset status of app's rating.
                            mDatabaseHelper.setRating(localActivityKey, getCurrentUser(), new RatingPropertiesBean(serverRating.intValue(), RatingStatus.NO_CHANGE));
                        }
                    } else {
                        // Server-side rating is null => other client has removed rating and user has changed rating in app. App wins?
                    }
                    break;
                case NO_CHANGE:
                    if (serverRating != null && appRating != null) {
                        if (serverRating.intValue() != appRating.getRating()) {
                            // Rating has not been changed in the app since the last syncronization but it has changed on the server. Update the app's rating to the server's rating.
                            mDatabaseHelper.setRating(localActivityKey, getCurrentUser(), new RatingPropertiesBean(serverRating.intValue(), RatingStatus.NO_CHANGE));
                        } else {
                            // App and server has same value for rating, and it has not been changed.
                        }
                    } else {
                        // Server-side rating is null, but the app has a rating. Does this mean that the rating has been removed by another client? Then the rating should (probably?) be removed in the app's database.
                    }
                    break;
                case REMOVED:
                    if (serverRating == null) {
                        // Rating "unset" in app and missing on server. Remove rating entry in app's database. No need to remove anything on server since it is already remove there!
                        mDatabaseHelper.removeRating(localActivityKey, getCurrentUser());
                    } else {
                        // Rating exists on server but, by specification, in this case the app "wins" and the rating should (upon next syncronization) be removed from the server.
                    }
                    break;
            }
        }
    }

    @Override
    public void unsetRating(ActivityKey activityKey, UserKey userKey) {
        Rating currentRating = readRating(activityKey, userKey);
        mDatabaseHelper.setRating(activityKey, userKey, new RatingPropertiesBean(currentRating.getRating(), RatingStatus.REMOVED));
    }

    private void processServerObject(CategoryBean category) {
        //TODO: refactor into separate method for updating database.
        switch (mDatabaseHelper.getLocalFreshness(category)) {
            case LOCAL_IS_MISSING:
                // Incoming data is a new (non-cached) activity. Add it to the local database.
                long id = mDatabaseHelper.createCategory(category);
                category.setId(id);
                break;
            case LOCAL_IS_OLD:
                // Incoming data is newer than cached data
                category.setId(mDatabaseHelper.getLocalIdByServerId(category));
                mDatabaseHelper.updateCategory(category, category);
                break;
            case LOCAL_IS_UP_TO_DATE:
                // No need to do anything
                category.setId(mDatabaseHelper.getLocalIdByServerId(category));
                break;
        }
    }

    private void processServerObject(MediaBean media) {
        //TODO: refactor into separate method for updating database.
        switch (mDatabaseHelper.getLocalFreshness(media)) {
            case LOCAL_IS_MISSING:
                // Incoming data is a new (non-cached) activity. Add it to the local database.
                long id = mDatabaseHelper.createMediaItem(media);
                media.setId(id);
                break;
            case LOCAL_IS_OLD:
                // Incoming data is newer than cached data
                media.setId(mDatabaseHelper.getLocalIdByServerId(media));
                mDatabaseHelper.updateMediaItem(media, media);
                break;
            case LOCAL_IS_UP_TO_DATE:
                // No need to do anything
                media.setId(mDatabaseHelper.getLocalIdByServerId(media));
                break;
        }
    }

    private void processServerObject(ReferenceBean reference) {
        //TODO: refactor into separate method for updating database.
        switch (mDatabaseHelper.getLocalFreshness(reference)) {
            case LOCAL_IS_MISSING:
                // Incoming data is a new (non-cached) activity. Add it to the local database.
                long id = mDatabaseHelper.createReference(reference);
                reference.setId(id);
                break;
            case LOCAL_IS_OLD:
                // Incoming data is newer than cached data
                reference.setId(mDatabaseHelper.getLocalIdByServerId(reference));
                mDatabaseHelper.updateReference(reference, reference);
                break;
            case LOCAL_IS_UP_TO_DATE:
                // No need to do anything
                reference.setId(mDatabaseHelper.getLocalIdByServerId(reference));
                break;
        }
    }

    @Override
    public List<CategoryBean> readCategories() throws UnauthorizedException {
        //TODO: Host name should not be kept in source code
        if (mCachedCategories == null) {
            try {
                String uri = getURL("categories");
                JSONArray array = getJSONArray(uri, null);
                mCachedCategories = new ArrayList<CategoryBean>();
                for (JSONObject obj : getJSONArrayAsList(array)) {
                    CategoryBean act = getCategoryBean(obj);

                    processServerObject(act);

                    mCachedCategories.add(act);
                }
            } catch (IOException | JSONException | UnhandledHttpResponseCodeException | UnauthorizedException e) {
                handleRemoteException(e);
                fireServiceDegradation(mContext.getString(R.string.remote_could_get_categories), e);
                mCachedCategories = super.readCategories();
            }
        }
        return mCachedCategories;
    }

    @Override
    public List<String> getSystemMessages(String key) {
        long lastGetSystemMessages = PreferenceManager.getDefaultSharedPreferences(mContext).getLong(getClass().getSimpleName() + ".LastGetSystemMessage", 0);
        long now = System.currentTimeMillis();
        if (now - lastGetSystemMessages > 24 * 60 * 60 * 1000) {

            // Time to see if new messages are available.

            // Check to see if the API's host name is saved in the database
            // since before. Otherwise the app will use a default host name
            // (this will definitely happen the first time the app is launched).
            String userSelectedHost = PreferencesUtil.getServerAddress(mContext);
            String host = userSelectedHost;
            if (TextUtils.isEmpty(host)) {
                List<String> hostNames = super.getSystemMessages(SYSTEM_MESSAGE_KEY_API_HOST);
                if (hostNames.size() > 0) {
                    host = hostNames.get(0);
                } else {
                    host = HOST + ":" + PORT;
                }
            }
            String uri = getURL("system_messages?valid=now_and_future", host);

            try {
                List<JSONObject> objects = getJSONArrayAsList(getJSONArray(uri, null));
                List<SystemMessageBean> messages = new ArrayList<>();
                for (JSONObject object : objects) {
                    messages.add(getSystemMessageBean(object));
                }

                // Save the retrieved messages to the database.
                mDatabaseHelper.setSystemMessages(messages);

                // Record current time so that the method knows when to check for new messages the next time.
                PreferenceManager.getDefaultSharedPreferences(mContext).edit().putLong(getClass().getSimpleName() + ".LastGetSystemMessage", new Date().getTime()).commit();
            } catch (IOException | JSONException | UnhandledHttpResponseCodeException | UnauthorizedException e) {
                try {
                    handleRemoteException(e);
                } catch (UnauthorizedException e1) {
                    // Ignore error. We know that this will never happen in this case since we know that the API always permits anyone to read system messages.
                }
                fireServiceDegradation(mContext.getString(R.string.remote_could_get_system_messages), e);
                return super.getSystemMessages(key);
            }

            initPreferenceAPIHosts();

            initPreferenceErrorReportRecipient();
        }

        // Return system messages from database
        return super.getSystemMessages(key);
    }

    private void initPreferenceErrorReportRecipient() {
        List<String> contactError = super.getSystemMessages(SYSTEM_MESSAGE_KEY_CONTACT_ERROR);
        PreferencesUtil.setErrorReportMailAddress(mContext, !contactError.isEmpty() ? contactError.get(0) : null);
    }

    private void initPreferenceAPIHosts() {
        HashSet<String> hosts = new HashSet<>();
        hosts.addAll(super.getSystemMessages(SYSTEM_MESSAGE_KEY_API_HOST));

        hosts.add(HOST + ":" + PORT);
//            if (BuildConfig.DEBUG || PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("is_allowed_to_use_test_api", false)) {
//                hosts.addAll(super.getSystemMessages("api:test-host"));
//                hosts.add(HOST_TEST + ":" + PORT_TEST);
//            }
        PreferencesUtil.setServerAddresses(mContext, hosts);
    }

    private SystemMessageBean getSystemMessageBean(JSONObject object) throws JSONException {
        return new SystemMessageBean(
                0L,
                object.getLong("id"),
                getServerRevisionId(object),
                object.getString("key"),
                getDate(object, "valid_from", null),
                getDate(object, "valid_from", null),
                object.getString("value")
        );
    }

    private String getURL(String noun) {
        String host = getRemoteHost();
        return getURL(noun, host);
    }

    private String getURL(String noun, String remoteHost) {
        return "http://" + remoteHost + "/api/v1/" + noun;
    }

    private JSONArray getJSONArray(String uri, JSONObject body) throws IOException, JSONException, UnauthorizedException, UnhandledHttpResponseCodeException {
        StopWatch stopWatch = new StopWatch("getJSONArray from " + uri);
        String s = readUrlAsString(uri, body != null ? body.toString() : null, HttpMethod.GET);
        stopWatch.logEvent("Fetched " + s.length() + " characters of data");
        JSONArray jsonArray = (JSONArray) new JSONTokener(s).nextValue();
        stopWatch.logEvent("Parsed data");
        LogUtil.d(RemoteActivityRepoImpl.class.getName(), stopWatch.getSummary());
        return jsonArray;
    }

    private JSONObject getJSONObject(String uri, JSONObject body, HttpMethod method) throws IOException, JSONException, UnauthorizedException, UnhandledHttpResponseCodeException {
        String s = readUrlAsString(uri, body != null ? body.toString() : null, method);
        return (JSONObject) new JSONTokener(s).nextValue();
    }

    private CategoryBean getCategoryBean(JSONObject obj) throws JSONException {
        ObjectIdentifierBean iconMediaKey = null;
        if (obj.has("media_file")) {
            try {
                MediaBean iconMedia = getMediaFileBean(obj.getJSONObject("media_file"));
                processServerObject(iconMedia);
                iconMediaKey = iconMedia != null ? new ObjectIdentifierBean(iconMedia) : null;
            } catch (URISyntaxException e) {
                LogUtil.i(RemoteActivityRepoImpl.class.getName(), "Could not parse URI in category's media item", e);
            }
        }
        CategoryBean cat = new CategoryBean(
                obj.getString("group"),
                obj.getString("name"),
                0L,
                obj.getInt("id"),
                getServerRevisionId(obj),
                iconMediaKey,
                obj.has("activities_count") && obj.getInt("activities_count") >= 0 ? obj.getInt("activities_count") : null);
        cat.setPublishable(false);
        return cat;
    }

    /**
     * Returns a number identifying the supplied object. The object's
     * highest/greatest identifier is returned if the object has multiple
     * identifiers.
     */
    private long getServerRevisionId(JSONObject obj) {
        long revId = 0;
        if (obj.has("revision_id")) {
            revId = Math.max(revId, obj.optLong("revision_id", new Date().getTime()));
        }
        if (obj.has("_updated_at")) {
            revId = Math.max(revId, getDate(obj, "_updated_at").getTime());
        }
        if (obj.has("updated_at")) {
            revId = Math.max(revId, getDate(obj, "updated_at").getTime());
        }
        if (obj.has("_created_at")) {
            revId = Math.max(revId, getDate(obj, "_created_at").getTime());
        }
        if (obj.has("created_at")) {
            revId = Math.max(revId, getDate(obj, "created_at").getTime());
        }
        return revId;
    }

    private ServerActivityBean getActivityBean(JSONObject obj) throws JSONException {
        ObjectIdentifierBean ownerId = null;//new ObjectIdentifierBean(mDatabaseHelper.getOrCreateUser(new UserPropertiesBean(null, null, obj.getLong("owner_id"), UNKNOWN_SERVER_REVISION_ID, false)));
        ServerActivityBean act = new ServerActivityBean(ownerId,
                0L,
                obj.getInt("id"),
                getServerRevisionId(obj),
                false);
//        ActivityRevisionBean revision = new ActivityRevisionBean(obj.getString("title"), false, new ObjectIdentifierBean(act.getId()), 0L);

        act.setName(obj.getString("name"));
//        act.setServerId(obj.getInt("id"));
//        act.setServerRevisionId(obj.getInt("revision_id"));
        act.setFeatured(obj.optBoolean("featured", false));
        act.setTimeActivity(getMinMaxRange(obj, "time"));
        act.setTimePreparation(getMinMaxRange(obj, "preparation"));
        act.setAges(getMinMaxRange(obj, "age"));
        act.setParticipants(getMinMaxRange(obj, "participants"));

        for (JSONObject jsonObject : getJSONObjectList(obj, "categories")) {
            CategoryBean categoryBean = getCategoryBean(jsonObject);
            processServerObject(categoryBean);
            act.getCategories().add(categoryBean);
        }

        for (JSONObject jsonObject : getJSONObjectList(obj, "references")) {
            try {
                ReferenceBean referenceBean = getReferenceBean(jsonObject);
                processServerObject(referenceBean);
                act.getReferences().add(referenceBean);
            } catch (URISyntaxException e) {
                LogUtil.i(RemoteActivityRepoImpl.class.getName(), "Could not parse URI in activity reference", e);
            }
        }

        for (JSONObject jsonObject : getJSONObjectList(obj, "media_files")) {
            try {
                MediaBean mediaFile = getMediaFileBean(jsonObject);
                processServerObject(mediaFile);
                act.getMediaItems().add(mediaFile);
            } catch (URISyntaxException e) {
                LogUtil.i(RemoteActivityRepoImpl.class.getName(), "Could not parse URI in activity media item", e);
            }
        }

        if (obj.has("_related")) {
            List<ActivityKey> related = new ArrayList<>();
            for (JSONObject jsonObject : getJSONObjectList(obj, "_related")) {
                ActivityKey key = new ObjectIdentifierBean(-jsonObject.getLong("related_activity_id"));
                related.add(key);
            }
            act.setRelatedActivitiesKeys(related);
        }

        act.setSafety(obj.optString("descr_safety", null));
        act.setDescription(obj.optString("descr_main", null));
        act.setIntroduction(obj.optString("descr_introduction", null));
        act.setMaterial(obj.optString("descr_material", null));
        act.setPreparation(obj.optString("descr_prepare", null));
        act.setDateCreated(getDate(obj, "created_at"));
        act.setFavouritesCount(obj.has("_favourite_count") && !obj.isNull("_favourite_count") ? obj.getInt("_favourite_count") : null);
        act.setRatingAverage(obj.has("_ratings_average") && !obj.isNull("_ratings_average") ? obj.getDouble("_ratings_average") : null);
        act.setMyRating(obj.has("_my_rating") && !obj.isNull("_my_rating") ? obj.getDouble("_my_rating") : null);
//        act.addRevisions(act);
        return act;
    }

    private MediaBean getMediaFileBean(JSONObject jsonObject) throws JSONException, URISyntaxException {
        return new MediaBean(
                new URI(jsonObject.getString("uri")),
                jsonObject.getString("mime_type"),
                0L,
                jsonObject.getLong("id"),
                0L,
                false
        );
    }

    private ReferenceBean getReferenceBean(JSONObject jsonObject) throws JSONException, URISyntaxException {
        return new ReferenceBean(
                0L,
                jsonObject.getLong("id"),
                0L,
                new URI(jsonObject.getString("uri")),
                jsonObject.getString("description"));
    }

    private Date getDate(JSONObject obj, String fieldName) {
        return getDate(obj, fieldName, new Date());
    }

    private Date getDate(JSONObject obj, String fieldName, Date defaultDate) {
        try {
            if (obj.has(fieldName)) {
                return API_DATE_FORMAT.parse(obj.getString(fieldName));
            } else {
                LogUtil.d(RemoteActivityRepoImpl.class.getName(), "JSON object does not have a field called " + fieldName + ". Will use current date instead.");
            }
        } catch (JSONException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not parse " + fieldName + ". Will use current date instead. " + e.getMessage() + ".");
        } catch (ParseException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not parse " + fieldName + ". Will use current date instead. " + e.getMessage() + ".");
        }
        return defaultDate;
    }

    private List<JSONObject> getJSONObjectList(JSONObject obj, String arrayAttributeName) throws JSONException {
        if (obj.has(arrayAttributeName)) {
            return getJSONArrayAsList(obj.getJSONArray(arrayAttributeName));
        } else {
            LogUtil.d(RemoteActivityRepoImpl.class.getName(), "Will return empty list instead of the (missing) array '" + arrayAttributeName + "'.");
            return Collections.emptyList();
        }
    }

    private List<JSONObject> getJSONArrayAsList(JSONArray jsonArray) throws JSONException {
        List<JSONObject> list = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getJSONObject(i));
        }
        return list;
    }

    private IntegerRange getMinMaxRange(JSONObject obj, final String field) throws JSONException {
        List<String> minValues = getStrings(obj, field + "_min");
        int min = Integer.MAX_VALUE;
        for (String minValue : minValues) {
            try {
                min = Math.min(Integer.parseInt(minValue), min);
            } catch (NumberFormatException e) {
                LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not parse string as number", e);
            }
        }
        List<String> maxValues = getStrings(obj, field + "_max");
        int max = Integer.MIN_VALUE;
        for (String maxValue : maxValues) {
            try {
                max = Math.max(Integer.parseInt(maxValue), max);
            } catch (NumberFormatException e) {
                LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not parse string as number", e);
            }
        }
        return new IntegerRange(min, max);
    }

    private List<String> getStrings(JSONObject obj, String field) throws JSONException {
        List<String> values = new ArrayList<String>();
        if (obj.optJSONArray(field) != null) {
            JSONArray categories = obj.optJSONArray(field);
            for (int x = 0; x < categories.length(); x++) {
                values.add(categories.getString(x));
            }
        } else if (obj.optString(field, null) != null) {
            values.add(obj.optString(field, null));
        }
        return values;
    }

    private String readUrlAsString(String urlSpec, String body, HttpMethod method) throws IOException, UnhandledHttpResponseCodeException, UnauthorizedException {
        byte[] data = readUrlAsBytes(urlSpec, body, method);
        if (data != null) {
            String s = new String(data);
            LogUtil.d(RemoteActivityRepoImpl.class.getName(), "Server response: " + s);
            return s;
        } else {
            LogUtil.d(RemoteActivityRepoImpl.class.getName(), "Server response contained no data");
            return null;
        }
    }

    private byte[] readUrlAsBytes(String urlSpec, String body, HttpMethod method) throws IOException, UnauthorizedException, UnhandledHttpResponseCodeException {
        try {
            URL url = new URL(urlSpec);
            try {
                return readUrl(body, method, url);
            } catch (SocketTimeoutException e) {
                URL urlDefaultHost = new URL(
                        url.getProtocol(),
                        HOST,
                        PORT,
                        url.getFile());
                if (!url.getHost().equals(urlDefaultHost.getHost())) {
                    return readUrl(body, method, urlDefaultHost);
                } else {
                    throw e;
                }
            }
        } catch (UnauthorizedException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Server said that user is unauthorized");
//            if (getAPIKey() == null) {
//                // User is unauthorized and has no API key, so the first this to try is to create an API key!
//                LogUtil.i(RemoteActivityRepoImpl.class.getName(), "Server said that user is unauthorized and there is no API key assigned to the app. Try to create one.");
//                synchronized (this) {
//                    /*
//                     * Double-check condition in case multiple "search threads"
//                     * try to send unauthorized requests at the same time. Both
//                     * thread will eventually enter the synchronized block but
//                     * the second one will exit immediately since the first
//                     * thread (presumably) created an API key and stored it.
//                     */
//                    if (getAPIKey() == null) {
//                        if (createAnonymousAPIUser()) {
//                            LogUtil.i(RemoteActivityRepoImpl.class.getName(), "API " + getAPIKey() + " key has been created.");
//                        } else {
//                            LogUtil.i(RemoteActivityRepoImpl.class.getName(), "Could not create API key");
//                        }
//                    }
//                }
//                if (getAPIKey() != null) {
//                    LogUtil.d(RemoteActivityRepoImpl.class.getName(), "API key (" + getAPIKey() + ") exists and request will be resent.");
//                    return readUrl(urlSpec, body, method);
//                }
//            }
            throw e;
        }
    }

    private byte[] readUrl(final String body, HttpMethod method, URL url) throws IOException, UnauthorizedException, UnhandledHttpResponseCodeException {
        HttpRequest request = new HttpRequest(url, method);

        String installationId = InstallationProperties.getInstance(mContext).getId().toString();
        request.setHeader(HTTP_HEADER_X_ANDROID_APP_INSTALLATION_ID, installationId);
        request.setHeader(HTTP_HEADER_ACCEPT_ENCODING, HttpRequest.HEADER_CONTENT_ENCODING_GZIP);

        Credentials credentials = mCredentialsMap.get(getCurrentUserId());
        if (credentials != null) {
            String authHeaderValue = "Token token=\"" + credentials.getToken() + "\", type=\"" + credentials.getType() + "\"";
            request.setHeader(HttpRequest.HEADER_AUTHORIZATION, authHeaderValue);
            LogUtil.d(HttpRequest.class.getName(), "Header " + HttpRequest.HEADER_AUTHORIZATION + ": " + authHeaderValue);
        }

        LogUtil.d(HttpRequest.class.getName(), "Sending request to " + url.toExternalForm());

        if (body != null) {
            request.setHeader(HTTP_HEADER_CONTENT_TYPE, "application/json; charset=" + DEFAULT_REQUEST_BODY_ENCODING);
        }

        RequestBodyStreamHandler requestHandler = new StringRequestBodyStreamHandler(body, Charset.forName(DEFAULT_REQUEST_BODY_ENCODING));
        ResponseStreamHandler<byte[]> responseHandler = new ByteArrayResponseStreamHandler();
        HttpResponse<byte[]> response = null;
        ResponseHeadersValidator responseHeadersValidator = new ResponseHeadersValidator() {
            @Override
            public void validate(Map<String, List<String>> headers) throws HeaderException {
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    if (!entry.getValue().isEmpty() && HTTP_HEADER_API_KEY.equals(entry.getKey())) {
                        mCredentialsMap.put(
                                getCurrentUserId(),
                                new Credentials(entry.getValue().get(0), API_TOKEN_TYPE_API_KEY));
                    }
                }
            }
        };
        try {
            response = request.run(responseHandler, requestHandler, responseHeadersValidator);
        } catch (SocketTimeoutException e1) {
            timeoutCounter++;
            throw e1;
        } catch (HeaderException e) {
            // Not possible since above ResponseHeadersValidator never throws such HeaderException
        }
        return response.getBody();
    }

    private Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    private UserKey getCurrentUser() {
        return CredentialsManager.getInstance(mContext).getCurrentUser();
    }

/*
    private String getAPIKey() {
        return PreferencesUtil.getInstance(mContext).getCurrentUser() != null ? mDatabaseHelper.readUser(PreferencesUtil.getInstance(mContext).getCurrentUser()).getAPIKey() : null;
    }
*/

    private class NegativeIdFixActivityList extends ActivityList {
        @Override
        public Activity get(ActivityKey activityKey) {
            return super.get(fixActivityKey(activityKey));
        }
    }

    /**
     * 1. Get list with local id, server_id and server_revision_id. This list can be cached and updated by DatabaseHelper.
     * 2. Add new activities, i.e. incoming activities whose server_id is not in the list.
     * 3. Update existing activities, i.e. incoming activities whose server_revision_id is greater than the locally stored value.
     */
/*
    public void updateActivities(List<ActivityBean> incomingActivities) {
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        for (ActivityBean activity : incomingActivities) {
            IdCacheEntry entry = mActivityIdCache.getEntryByServerId(activity.getServerId());
            if (entry != null) {
                // Activity is cached
                if (entry.mServerRevisionId < activity.getServerRevisionId()) {
                    // Incoming data is newer than cached data
                    updateActivity(activity);
                } else {
                    // No need to do anything
                }
            } else {
                // Incoming data is a new (non-cached) activity. Add it to the local database.
                createActivity(activity, activity.getOwner().getId());
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
*/

}

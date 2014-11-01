package se.devscout.android.model.repo.remote;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import se.devscout.android.controller.fragment.TitleActivityFilterVisitor;
import se.devscout.android.model.*;
import se.devscout.android.model.repo.sql.SQLiteActivityRepo;
import se.devscout.android.util.*;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RemoteActivityRepoImpl extends SQLiteActivityRepo {
    static final String HOST = "devscout.mikaelsvensson.info:10081";
    private static final String DEFAULT_REQUEST_BODY_ENCODING = "utf-8";
    private static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
    private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HTTP_HEADER_X_ANDROID_APP_INSTALLATION_ID = "X-AndroidAppInstallationId";
    private static final long UNKNOWN_SERVER_REVISION_ID = 0L;
    private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
    private static RemoteActivityRepoImpl ourInstance;
    private final Context mContext;

    public static RemoteActivityRepoImpl getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = new RemoteActivityRepoImpl(ctx);
        }
        return ourInstance;
    }

    public RemoteActivityRepoImpl(Context ctx) {
        super(ctx);
        mContext = ctx;
    }

    @Override
    public Activity createActivity(ActivityProperties properties) {
        //TODO: implement/overload/fix this method. Low priority.
        return super.createActivity(properties);
    }

    @Override
    public Reference createReference(ActivityKey key, ReferenceProperties properties) {
        //TODO: this method probably does not have to be overloaded, provided references are automatically created by the server when activities are created/updated. Low priority.
        return super.createReference(key, properties);
    }

    @Override
    public SearchHistory createSearchHistory(HistoryProperties<SearchHistoryData> properties, UserKey userKey) {
        //TODO: this method probably does not have to be overloaded, as long as search history should only be stored server-side as well. Low priority.
        return super.createSearchHistory(properties, userKey);
    }

    @Override
    public void deleteActivity(ActivityKey key) {
        //TODO: implement/overload/fix this method. Low priority.
        super.deleteActivity(key);
    }

    @Override
    public void deleteReference(ActivityKey key, ReferenceKey referenceKey) {
        //TODO: this method probably does not have to be overloaded, provided references are automatically created by the server when activities are created/updated. Low priority.
        super.deleteReference(key, referenceKey);
    }

    @Override
    public void deleteSearchHistory(int itemsToKeep) {
        //TODO: this method probably does not have to be overloaded, as long as search history should only be stored server-side as well.
        super.deleteSearchHistory(itemsToKeep);
    }

    @Override
    public boolean createAnonymousAPIUser() {
        try {
            String uri = "http://" + HOST + "/api/v1/users";
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

    @Override
    public boolean isFavourite(ActivityKey activityKey, UserKey userKey) {
        //TODO: implement/overload/fix this method. High priority.
        return super.isFavourite(activityKey, userKey);
    }

    @Override
    public Activity readActivity(ActivityKey key) {
        //TODO: implement/overload/fix this method. High priority.
        return super.readActivity(key);
/*
        //TODO: Host name should not be kept in source code
        try {
            String uri = "http://" + HOST + "/api/v1/activities/" + key.getId();
            JSONObject obj = getJSONObject(uri);

            ActivityBean act = getActivityBean(obj);

            processServerObject(act);

            return act;
        } catch (IOException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not query server", e);
            return super.readActivity(key);
        } catch (JSONException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Error parsing JSON response", e);
            return super.readActivity(key);
        }
*/
    }

    @Override
    public Category readCategoryFull(CategoryKey key) {
        //TODO: implement/overload/fix this method. High priority.
        return super.readCategoryFull(key);
    }

    @Override
    public Activity readActivityFull(ActivityKey key) {
        //TODO: implement/overload/fix this method. High priority.
        return super.readActivityFull(key);
    }

    @Override
    public List<Reference> readReferences(ActivityKey key) {
        //TODO: implement/overload/fix this method. High priority.
        return super.readReferences(key);
    }

    @Override
    public List<? extends SearchHistory> readSearchHistory(int limit, UserKey userKey) {
        return super.readSearchHistory(limit, userKey);
    }

    @Override
    public void setFavourite(ActivityKey activityKey, UserKey userKey) {
        super.setFavourite(activityKey, userKey);
        createSendSetFavouritesTask().execute();
    }

    @Override
    public void unsetFavourite(ActivityKey activityKey, UserKey userKey) {
        super.unsetFavourite(activityKey, userKey);
        createSendSetFavouritesTask().execute();
    }

    private AsyncTask<Void, Void, Void> createSendSetFavouritesTask() {
        return new AsyncTask<Void, Void, Void>() {
            {
                LogUtil.initExceptionLogging(mContext);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    sendSetFavouritesRequest();
                } catch (IOException e) {
                    LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not send favourites to server", e);
                } catch (UnauthorizedException e) {
                    LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not send favourites to server", e);
                } catch (UnhandledHttpResponseCodeException e) {
                    LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not send favourites to server", e);
                } catch (Throwable e) {
                    LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not send favourites to server due to unexpected problem.", e);
                }
                return null;
            }
        };
    }

    public void sendSetFavouritesRequest() throws IOException, UnauthorizedException, UnhandledHttpResponseCodeException {
        List<ActivityBean> favourites = super.findActivity(getFilterFactory().createIsUserFavouriteFilter(PreferencesUtil.getInstance(mContext).getCurrentUser()));
//        Set<ActivityKey> favourites = mDatabaseHelper.getFavourites(PreferencesUtil.getInstance(mContext).getCurrentUser());
        JSONArray jsonArray = new JSONArray();
        for (ActivityBean key : favourites) {
            jsonArray.put(key.getServerId());
        }
        String uri = "http://" + HOST + "/api/v1/favourites";

        readUrlAsBytes(uri, new JSONObject(Collections.singletonMap("id", jsonArray)).toString(), HttpMethod.PUT);
    }

    @Override
    public ActivityProperties updateActivity(ActivityKey key, ActivityProperties properties) {
        //TODO: implement/overload/fix this method. Low priority.
        return super.updateActivity(key, properties);
    }

    @Override
    public List<ActivityBean> findActivity(ActivityFilter condition) throws UnauthorizedException {
        StopWatch stopWatch = new StopWatch("findActivity " + condition.toString(new TitleActivityFilterVisitor(mContext)));
        URIBuilderActivityFilterVisitor visitor = new ApiV1Visitor();
        String uri = condition.toAPIRequest(visitor).toString();
        try {
            stopWatch.logEvent("Preparing request");
            JSONArray array = getJSONArray(uri, null);
            stopWatch.logEvent("Sending query to server and reading response");
            ArrayList<ActivityBean> result = new ArrayList<ActivityBean>();
            stopWatch.logEvent("Parsed JSON");
            for (JSONObject obj : getJSONArrayAsList(array)) {
                ActivityBean act = getActivityBean(obj);

                processServerObject(act);

                result.add(act);
            }
            stopWatch.logEvent("Saving all returned data in local database");
            return result;
        } catch (IOException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not query server", e);
            return super.findActivity(condition);
        } catch (JSONException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Error parsing JSON response", e);
            return super.findActivity(condition);
        } catch (UnhandledHttpResponseCodeException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Cannot handle server response.", e);
            return super.findActivity(condition);
        } finally {
            stopWatch.logEvent("The last things");
            LogUtil.i(RemoteActivityRepoImpl.class.getName(), stopWatch.getSummary());
        }
    }

    private void processServerObject(ActivityBean act) {
        //TODO: refactor into separate method for updating database.
        switch (mDatabaseHelper.getLocalFreshness(act)) {
            case LOCAL_IS_MISSING:
                // Incoming data is a new (non-cached) activity. Add it to the local database.
                long id = mDatabaseHelper.createActivity(act);
                act.setId(id);
                break;
            case LOCAL_IS_OLD:
                // Incoming data is newer than cached data
                act.setId(mDatabaseHelper.getLocalIdByServerId(act));
                mDatabaseHelper.updateActivity(act, act);
                break;
            case LOCAL_IS_UP_TO_DATE:
                // No need to do anything
                act.setId(mDatabaseHelper.getLocalIdByServerId(act));
                break;
        }
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
        try {
            String uri = "http://" + HOST + "/api/v1/categories";
            JSONArray array = getJSONArray(uri, null);
            ArrayList<CategoryBean> result = new ArrayList<CategoryBean>();
            for (JSONObject obj : getJSONArrayAsList(array)) {
                CategoryBean act = getCategoryBean(obj);

                processServerObject(act);

                result.add(act);
            }
            return result;
        } catch (IOException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Could not query server", e);
            return super.readCategories();
        } catch (JSONException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Error parsing JSON response", e);
            return super.readCategories();
        } catch (UnhandledHttpResponseCodeException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Cannot handle server response.", e);
            return super.readCategories();
        }
    }

    private JSONArray getJSONArray(String uri, JSONObject body) throws IOException, JSONException, UnauthorizedException, UnhandledHttpResponseCodeException {
        StopWatch stopWatch = new StopWatch("getJSONArray from " + uri);
        String s = readUrlAsString(uri, body != null ? body.toString() : null, HttpMethod.GET);
        stopWatch.logEvent("Fetched data");
        JSONArray jsonArray = (JSONArray) new JSONTokener(s).nextValue();
        stopWatch.logEvent("Parsed data");
        LogUtil.i(RemoteActivityRepoImpl.class.getName(), stopWatch.getSummary());
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
                iconMediaKey);
        cat.setPublishable(false);
        return cat;
    }

    private long getServerRevisionId(JSONObject obj) {
        if (obj.has("revision_id")) {
            return obj.optLong("revision_id", new Date().getTime());
        } else if (obj.has("updated_at")) {
            return getDate(obj, "updated_at").getTime();
        } else {
            return getDate(obj, "created_at").getTime();
        }
    }

    private ActivityBean getActivityBean(JSONObject obj) throws JSONException {
        ObjectIdentifierBean ownerId = null;//new ObjectIdentifierBean(mDatabaseHelper.getOrCreateUser(new UserPropertiesBean(null, null, obj.getLong("owner_id"), UNKNOWN_SERVER_REVISION_ID, false)));
        ActivityBean act = new ActivityBean(ownerId,
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

        act.setSafety(obj.optString("descr_safety", null));
        act.setDescription(obj.optString("descr_main", null));
        act.setIntroduction(obj.optString("descr_introduction", null));
        act.setMaterial(obj.optString("descr_material", null));
        act.setPreparation(obj.optString("descr_prepare", null));
        act.setDateCreated(getDate(obj, "created_at"));
        act.setFavouritesCount(obj.has("favourite_count") ? obj.getInt("favourite_count") : null);
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
        return new Date();
    }

    private List<JSONObject> getJSONObjectList(JSONObject obj, String arrayAttributeName) throws JSONException {
        return getJSONArrayAsList(obj.getJSONArray(arrayAttributeName));
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
            return readUrl(urlSpec, body, method);
        } catch (UnauthorizedException e) {
            LogUtil.e(RemoteActivityRepoImpl.class.getName(), "Server said that user is unauthorized");
            if (getAPIKey() == null) {
                // User is unauthorized and has no API key, so the first this to try is to create an API key!
                LogUtil.i(RemoteActivityRepoImpl.class.getName(), "Server said that user is unauthorized and there is no API key assigned to the app. Try to create one.");
                synchronized (this) {
                    /*
                     * Double-check condition in case multiple "search threads"
                     * try to send unauthorized requests at the same time. Both
                     * thread will eventually enter the synchronized block but
                     * the second one will exit immediately since the first
                     * thread (presumably) created an API key and stored it.
                     */
                    if (getAPIKey() == null) {
                        if (createAnonymousAPIUser()) {
                            LogUtil.i(RemoteActivityRepoImpl.class.getName(), "API " + getAPIKey() + " key has been created.");
                        } else {
                            LogUtil.i(RemoteActivityRepoImpl.class.getName(), "Could not create API key");
                        }
                    }
                }
                if (getAPIKey() != null) {
                    LogUtil.d(RemoteActivityRepoImpl.class.getName(), "API key (" + getAPIKey() + ") exists and request will be resent.");
                    return readUrl(urlSpec, body, method);
                }
            }
            throw e;
        }
    }

    private byte[] readUrl(String urlSpec, final String body, HttpMethod method) throws IOException, UnauthorizedException, UnhandledHttpResponseCodeException {
        URL url = new URL(urlSpec);
        HttpRequest request = new HttpRequest(url, method);

        String installationId = InstallationProperties.getInstance(mContext).getId().toString();
        request.setHeader(HTTP_HEADER_X_ANDROID_APP_INSTALLATION_ID, installationId);

        String apiKey = getAPIKey();
        if (apiKey != null) {
            request.setHeader(HTTP_HEADER_AUTHORIZATION, "Token token=\"" + apiKey + "\"");
        }

        LogUtil.d(HttpRequest.class.getName(), "Sending request to " + url.toExternalForm() + " (API key: " + apiKey + ")");

        if (body != null) {
            request.setHeader(HTTP_HEADER_CONTENT_TYPE, "application/json; charset=" + DEFAULT_REQUEST_BODY_ENCODING);
        }

        RequestBodyStreamHandler requestHandler = new StringRequestBodyStreamHandler(body, Charset.forName(DEFAULT_REQUEST_BODY_ENCODING));
        ResponseStreamHandler<byte[]> responseHandler = new ByteArrayResponseStreamHandler();
        HttpResponse<byte[]> response = request.run(responseHandler, requestHandler);
        return response.getBody();
    }

    private String getAPIKey() {
        return PreferencesUtil.getInstance(mContext).getCurrentUser() != null ? mDatabaseHelper.readUser(PreferencesUtil.getInstance(mContext).getCurrentUser()).getAPIKey() : null;
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

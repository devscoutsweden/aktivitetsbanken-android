package se.devscout.android.model.repo.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import se.devscout.android.model.IntegerRangePojo;
import se.devscout.android.model.ObjectIdentifierPojo;
import se.devscout.android.model.repo.LocalActivity;
import se.devscout.android.model.repo.LocalCategory;
import se.devscout.android.model.repo.sql.SQLiteActivityRepo;
import se.devscout.android.util.InstallationProperties;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RemoteActivityRepoImpl extends SQLiteActivityRepo {
    static final String HOST = "infinite-forest-4832.herokuapp.com";
    private static final String DEFAULT_REQUEST_BODY_ENCODING = "utf-8";
    private static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
    private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HTTP_HEADER_X_ANDROID_APP_INSTALLATION_ID = "X-AndroidAppInstallationId";
    private static final String PREFERENCE_API_KEY = "api_key";
    private static final long UNKNOWN_SERVER_REVISION_ID = 0L;
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
    public SearchHistory createSearchHistory(HistoryProperties<SearchHistoryData> properties) {
        //TODO: this method probably does not have to be overloaded, as long as search history should only be stored server-side as well. Low priority.
        return super.createSearchHistory(properties);
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
    public Boolean createAnonymousAPIUser() {
        try {
            String uri = "http://" + HOST + "/api/v1/users";
            JSONObject body = new JSONObject();
            body.put("display_name", "android-app-" + InstallationProperties.getInstance(mContext).getId());
            JSONObject response = getJSONObject(uri, body, HttpMethod.POST);
            String apiKey = response.getString(PREFERENCE_API_KEY);
            return PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(PREFERENCE_API_KEY, apiKey).commit();
        } catch (JSONException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Could not create anonymous API user because of JSON problem.", e);
            return false;
        } catch (IOException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Could not create anonymous API user because of I/O problem.", e);
            return false;
        } catch (UnauthorizedException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Could not create anonymous API user because of authorization problem.", e);
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

            LocalActivity act = getLocalActivity(obj);

            processActivityFromServer(act);

            return act;
        } catch (IOException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Could not query server", e);
            return super.readActivity(key);
        } catch (JSONException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Error parsing JSON response", e);
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
    public List<? extends SearchHistory> readSearchHistory(int limit) {
        return super.readSearchHistory(limit);
    }

    @Override
    public void setFavourite(ActivityKey activityKey, UserKey userKey) {
        //TODO: implement/overload/fix this method. High priority.
        super.setFavourite(activityKey, userKey);
    }

    @Override
    public void unsetFavourite(ActivityKey activityKey, UserKey userKey) {
        //TODO: implement/overload/fix this method. High priority.
        super.unsetFavourite(activityKey, userKey);
    }

    @Override
    public ActivityProperties updateActivity(ActivityKey key, ActivityProperties properties) {
        //TODO: implement/overload/fix this method. Low priority.
        return super.updateActivity(key, properties);
    }

    @Override
    public List<LocalActivity> findActivity(ActivityFilter condition) throws UnauthorizedException {
        URIBuilderActivityFilterVisitor visitor = new ApiV1Visitor();
        String uri = condition.toAPIRequest(visitor).toString();
        try {
            JSONArray array = getJSONArray(uri, null);
            ArrayList<LocalActivity> result = new ArrayList<LocalActivity>();
            for (JSONObject obj : getJSONArrayAsList(array)) {
                LocalActivity act = getLocalActivity(obj);

                processActivityFromServer(act);

                result.add(act);
            }
            return result;
        } catch (IOException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Could not query server", e);
            return super.findActivity(condition);
        } catch (JSONException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Error parsing JSON response", e);
            return super.findActivity(condition);
        }
    }

    private void processActivityFromServer(LocalActivity act) {
        //TODO: refactor into separate method for updating database.
        switch (mDatabaseHelper.getLocalActivityFreshness(act, true)) {
            case LOCAL_IS_MISSING:
                // Incoming data is a new (non-cached) activity. Add it to the local database.
                long id = mDatabaseHelper.createActivity(act);
                act.setId(id);
                break;
            case LOCAL_IS_OLD:
                // Incoming data is newer than cached data
                mDatabaseHelper.updateActivity(act, act);
                break;
            case LOCAL_IS_UP_TO_DATE:
                // No need to do anything
                break;
        }
    }

    @Override
    public List<LocalCategory> readCategories() throws UnauthorizedException {
        //TODO: Host name should not be kept in source code
        try {
            String uri = "http://" + HOST + "/api/v1/categories";
            JSONArray array = getJSONArray(uri, null);
            ArrayList<LocalCategory> result = new ArrayList<LocalCategory>();
            for (JSONObject obj : getJSONArrayAsList(array)) {
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject obj = array.getJSONObject(i);
                LocalCategory act = getLocalCategory(obj);
                result.add(act);
            }
            //TODO: add functionality for caching/storing incoming data in database. Check find(...) method for inspiration. High priority.
//            updateLocalDatabase(result);
            return result;
        } catch (IOException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Could not query server", e);
            return super.readCategories();
        } catch (JSONException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Error parsing JSON response", e);
            return super.readCategories();
        }
    }

    private JSONArray getJSONArray(String uri, JSONObject body) throws IOException, JSONException, UnauthorizedException {
        String s = readUrlAsString(uri, body, HttpMethod.GET);
        Log.i(RemoteActivityRepoImpl.class.getName(), "Server response: " + s);
        return (JSONArray) new JSONTokener(s).nextValue();
    }

    private JSONObject getJSONObject(String uri, JSONObject body) throws IOException, JSONException, UnauthorizedException {
        return getJSONObject(uri, body, HttpMethod.GET);
    }

    private JSONObject getJSONObject(String uri, JSONObject body, HttpMethod method) throws IOException, JSONException, UnauthorizedException {
        String s = readUrlAsString(uri, body, method);
        Log.i(RemoteActivityRepoImpl.class.getName(), "Server response: " + s);
        return (JSONObject) new JSONTokener(s).nextValue();
    }

    private LocalCategory getLocalCategory(JSONObject obj) throws JSONException {
        LocalCategory cat = new LocalCategory(
                obj.getString("group"),
                obj.getString("name"),
                0L,
                obj.getInt("id"),
                getServerRevisionId(obj));
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

    private LocalActivity getLocalActivity(JSONObject obj) throws JSONException {
        ObjectIdentifierPojo ownerId = null;//new ObjectIdentifierPojo(mDatabaseHelper.getOrCreateUser(new UserPropertiesPojo(null, null, obj.getLong("owner_id"), UNKNOWN_SERVER_REVISION_ID, false)));
        LocalActivity act = new LocalActivity(ownerId,
                0L,
                obj.getInt("id"),
                getServerRevisionId(obj),
                false);
//        LocalActivityRevision revision = new LocalActivityRevision(obj.getString("title"), false, new ObjectIdentifierPojo(act.getId()), 0L);

        act.setName(obj.getString("name"));
//        act.setServerId(obj.getInt("id"));
//        act.setServerRevisionId(obj.getInt("revision_id"));
        act.setFeatured(obj.optBoolean("featured", false));
        act.setTimeActivity(getMinMaxRange(obj, "time"));
        act.setTimePreparation(getMinMaxRange(obj, "preparation"));
        act.setAges(getMinMaxRange(obj, "age"));
        act.setParticipants(getMinMaxRange(obj, "participants"));

        for (JSONObject jsonObject : getJSONObjectList(obj, "categories")) {
            act.getCategories().add(getLocalCategory(jsonObject));
        }

        act.setSafety(obj.optString("descr_safety", null));
        act.setDescription(obj.optString("descr_main", null));
        act.setIntroduction(obj.optString("descr_introduction", null));
        act.setMaterial(obj.optString("descr_material", null));
        act.setPreparation(obj.optString("descr_prepare", null));
        act.setDateCreated(getDate(obj, "created_at"));
//        act.addRevisions(act);
        return act;
    }

    private Date getDate(JSONObject obj, String fieldName) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'").parse(obj.getString(fieldName));
        } catch (JSONException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Could not parse " + fieldName + ". Will use current date instead.", e);
        } catch (ParseException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Could not parse " + fieldName + ". Will use current date instead.", e);
        }
        return date;
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

    private IntegerRangePojo getMinMaxRange(JSONObject obj, final String field) throws JSONException {
        List<String> minValues = getStrings(obj, field + "_min");
        int min = Integer.MAX_VALUE;
        for (String minValue : minValues) {
            try {
                min = Math.min(Integer.parseInt(minValue), min);
            } catch (NumberFormatException e) {
                Log.e(RemoteActivityRepoImpl.class.getName(), "Could not parse string as number", e);
            }
        }
        List<String> maxValues = getStrings(obj, field + "_max");
        int max = Integer.MIN_VALUE;
        for (String maxValue : maxValues) {
            try {
                max = Math.max(Integer.parseInt(maxValue), max);
            } catch (NumberFormatException e) {
                Log.e(RemoteActivityRepoImpl.class.getName(), "Could not parse string as number", e);
            }
        }
        return new IntegerRangePojo(min, max);
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

    private String readUrlAsString(String urlSpec, JSONObject body, HttpMethod method) throws IOException, UnauthorizedException {
        return new String(readUrl(urlSpec, body, method));
    }

    private byte[] readUrl(String urlSpec, JSONObject body, HttpMethod method) throws IOException, UnauthorizedException {
        URL url = new URL(urlSpec);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try {
            httpURLConnection.setRequestMethod(method.name());

            String installationId = InstallationProperties.getInstance(mContext).getId().toString();
            httpURLConnection.addRequestProperty(HTTP_HEADER_X_ANDROID_APP_INSTALLATION_ID, installationId);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            if (preferences.contains(PREFERENCE_API_KEY)) {
                httpURLConnection.addRequestProperty(HTTP_HEADER_AUTHORIZATION, "Token token=\"" + preferences.getString(PREFERENCE_API_KEY, "") + "\"");
            }

            if (body != null) {
                httpURLConnection.addRequestProperty(HTTP_HEADER_CONTENT_TYPE, "application/json; charset=" + DEFAULT_REQUEST_BODY_ENCODING);

                // Writing to output string will send request
                httpURLConnection.getOutputStream().write(body.toString().getBytes(DEFAULT_REQUEST_BODY_ENCODING));
            }

            // Asking about response code will send request, if not already sent
            switch (httpURLConnection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_CREATED:
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int bytesRead = 0;
                    byte[] buffer = new byte[1024];
                    InputStream in = httpURLConnection.getInputStream();
                    while ((bytesRead = in.read(buffer)) > 0) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.close();
                    return out.toByteArray();
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    throw new UnauthorizedException();
                default:
                    return null;
            }

        } finally {
            httpURLConnection.disconnect();
        }
    }

    /**
     * 1. Get list with local id, server_id and server_revision_id. This list can be cached and updated by DatabaseHelper.
     * 2. Add new activities, i.e. incoming activities whose server_id is not in the list.
     * 3. Update existing activities, i.e. incoming activities whose server_revision_id is greater than the locally stored value.
     */
/*
    public void updateActivities(List<LocalActivity> incomingActivities) {
        SQLiteDatabase db = getDb();
        db.beginTransaction();
        for (LocalActivity activity : incomingActivities) {
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

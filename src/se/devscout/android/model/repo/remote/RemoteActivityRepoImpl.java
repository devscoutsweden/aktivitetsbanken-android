package se.devscout.android.model.repo.remote;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import se.devscout.android.model.IntegerRangePojo;
import se.devscout.android.model.repo.LocalActivity;
import se.devscout.android.model.repo.LocalCategory;
import se.devscout.android.model.repo.sql.DatabaseHelper;
import se.devscout.android.model.repo.sql.SQLiteActivityRepo;
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
    private static RemoteActivityRepoImpl ourInstance;

    public static RemoteActivityRepoImpl getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = new RemoteActivityRepoImpl(ctx);
        }
        return ourInstance;
    }

    public RemoteActivityRepoImpl(Context ctx) {
        super(ctx);
    }

    @Override
    public Activity create(ActivityProperties properties) {
        //TODO: implement/overload/fix this method.
        return super.create(properties);
    }

    @Override
    public Reference createReference(ActivityKey key, ReferenceProperties properties) {
        //TODO: this method probably does not have to be overloaded, provided references are automatically created by the server when activities are created/updated.
        return super.createReference(key, properties);
    }

    @Override
    public SearchHistory createSearchHistory(HistoryProperties<SearchHistoryData> properties) {
        //TODO: this method probably does not have to be overloaded, as long as search history should only be stored server-side as well.
        return super.createSearchHistory(properties);
    }

    @Override
    public void delete(ActivityKey key) {
        //TODO: implement/overload/fix this method.
        super.delete(key);
    }

    @Override
    public void deleteReference(ActivityKey key, ReferenceKey referenceKey) {
        //TODO: this method probably does not have to be overloaded, provided references are automatically created by the server when activities are created/updated.
        super.deleteReference(key, referenceKey);
    }

    @Override
    public void deleteSearchHistory(int itemsToKeep) {
        //TODO: this method probably does not have to be overloaded, as long as search history should only be stored server-side as well.
        super.deleteSearchHistory(itemsToKeep);
    }

    @Override
    public boolean isFavourite(ActivityKey activityKey, UserKey userKey) {
        //TODO: implement/overload/fix this method.
        return super.isFavourite(activityKey, userKey);
    }

    @Override
    public Activity read(ActivityKey key) {
        //TODO: implement/overload/fix this method.
        return super.read(key);
    }

    @Override
    public Category readCategoryFull(CategoryKey key) {
        //TODO: implement/overload/fix this method.
        return super.readCategoryFull(key);
    }

    @Override
    public Activity readFull(ActivityKey key) {
        //TODO: implement/overload/fix this method.
        return super.readFull(key);
    }

    @Override
    public List<Reference> readReferences(ActivityKey key) {
        //TODO: implement/overload/fix this method.
        return super.readReferences(key);
    }

    @Override
    public List<? extends SearchHistory> readSearchHistory(int limit) {
        return super.readSearchHistory(limit);
    }

    @Override
    public void setFavourite(ActivityKey activityKey, UserKey userKey) {
        //TODO: implement/overload/fix this method.
        super.setFavourite(activityKey, userKey);
    }

    @Override
    public void unsetFavourite(ActivityKey activityKey, UserKey userKey) {
        //TODO: implement/overload/fix this method.
        super.unsetFavourite(activityKey, userKey);
    }

    @Override
    public ActivityProperties update(ActivityKey key, ActivityProperties properties) {
        //TODO: implement/overload/fix this method.
        return super.update(key, properties);
    }

    @Override
    public List<LocalActivity> find(ActivityFilter condition) {
        URIBuilderActivityFilterVisitor visitor = new ApiV1Visitor();
        String uri = condition.toAPIRequest(visitor).toString();
        try {
            JSONArray array = getJSONArray(uri);
            ArrayList<LocalActivity> result = new ArrayList<LocalActivity>();
            for (JSONObject obj : getJSONArrayAsList(array)) {
                LocalActivity act = getLocalActivity(obj);

                //TODO: refactor into separate method for updating datbase.
                DatabaseHelper.IdCacheEntry entry = mDatabaseHelper.getActivityIdCache().getEntryByServerId(act.getServerId());
                if (entry != null) {
                    act.setId(entry.getId());
                    // Activity is cached
                    if (entry.getServerRevisionId() < act.getServerRevisionId()) {
                        // Incoming data is newer than cached data
                        mDatabaseHelper.updateActivity(act, act);
                    } else {
                        // No need to do anything
                    }
                } else {
                    // Incoming data is a new (non-cached) activity. Add it to the local database.
                    long id = mDatabaseHelper.createActivity(act);
                    act.setId(id);
                }

                result.add(act);
            }
            return result;
        } catch (IOException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Could not query server", e);
            return super.find(condition);
        } catch (JSONException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Error parsing JSON response", e);
            return super.find(condition);
        }
    }

    @Override
    public List<LocalCategory> readCategories() {
        //TODO: Host name should not be kept in source code
        Uri.Builder uriBuilder = Uri.parse("http://infinite-forest-4832.herokuapp.com/api/v1/categories").buildUpon();
        String uri = uriBuilder.build().toString();
        try {
            JSONArray array = getJSONArray(uri);
            ArrayList<LocalCategory> result = new ArrayList<LocalCategory>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                LocalCategory act = getLocalCategory(obj);
                result.add(act);
            }
            //TODO: add functionality for caching/storing incoming data in database. Check find(...) method for inspiration.
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

    private JSONArray getJSONArray(String uri) throws IOException, JSONException {
        String s = readUrlAsString(uri);
        Log.i(RemoteActivityRepoImpl.class.getName(), "Server response: " + s);
        return (JSONArray) new JSONTokener(s).nextValue();
    }

    private LocalCategory getLocalCategory(JSONObject obj) throws JSONException {
        LocalCategory cat = new LocalCategory(obj.getString("group"), obj.getString("name"), 0L, obj.getInt("id"));
        cat.setPublishable(false);
        return cat;
    }

    private LocalActivity getLocalActivity(JSONObject obj) throws JSONException {
        LocalActivity act = new LocalActivity(null, 0L, obj.getInt("id"), false);
//        LocalActivityRevision revision = new LocalActivityRevision(obj.getString("title"), false, new ObjectIdentifierPojo(act.getId()), 0L);

        act.setName(obj.getString("name"));
        act.setServerId(obj.getInt("id"));
        act.setServerRevisionId(obj.getInt("revision_id"));
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
        Date createdAt = new Date();
        try {
            createdAt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'").parse(obj.getString(fieldName));
        } catch (JSONException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Could not parse " + fieldName + ". Will use current date instead.", e);
        } catch (ParseException e) {
            Log.e(RemoteActivityRepoImpl.class.getName(), "Could not parse " + fieldName + ". Will use current date instead.", e);
        }
        return createdAt;
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

    private String readUrlAsString(String urlSpec) throws IOException {
        return new String(readUrl(urlSpec));
    }

    private byte[] readUrl(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = httpURLConnection.getInputStream();
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
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

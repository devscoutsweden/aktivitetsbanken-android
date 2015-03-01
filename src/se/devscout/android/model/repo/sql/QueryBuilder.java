package se.devscout.android.model.repo.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import se.devscout.android.model.IntegerRange;
import se.devscout.android.util.auth.CredentialsManager;
import se.devscout.server.api.AverageRatingFilter;
import se.devscout.server.api.activityfilter.CategoryFilter;
import se.devscout.server.api.activityfilter.OverallFavouriteActivitiesFilter;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.Range;
import se.devscout.server.api.model.UserKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QueryBuilder {
    private final Context mContext;

    private List<String> mWhere = new ArrayList<String>();
    private List<String> mWhereConditions = new ArrayList<String>();
    private List<String> mSelect = new ArrayList<String>();
    private List<String> mOrderBy = new ArrayList<String>();
    private StringBuilder mFrom = new StringBuilder();
    private int mLimitResultLength = 0;

    public QueryBuilder(Context context) {

        mContext = context;

        mSelect.add("a." + Database.activity.owner_id);
        mSelect.add("a." + Database.activity.id);
        mSelect.add("a." + Database.activity.server_id);
        mSelect.add("a." + Database.activity.server_revision_id);
        mSelect.add("a." + Database.activity.is_publishable);
        mSelect.add("a." + Database.activity.name);
        mSelect.add("a." + Database.activity.datetime_published);
        mSelect.add("a." + Database.activity.datetime_created);
        mSelect.add("a." + Database.activity.descr_material);
        mSelect.add("a." + Database.activity.descr_introduction);
        mSelect.add("a." + Database.activity.descr_prepare);
        mSelect.add("a." + Database.activity.descr_activity);
        mSelect.add("a." + Database.activity.descr_safety);
        mSelect.add("a." + Database.activity.descr_notes);
        mSelect.add("a." + Database.activity.age_min);
        mSelect.add("a." + Database.activity.age_max);
        mSelect.add("a." + Database.activity.participants_min);
        mSelect.add("a." + Database.activity.participants_max);
        mSelect.add("a." + Database.activity.time_min);
        mSelect.add("a." + Database.activity.time_max);
        mSelect.add("a." + Database.activity.featured);
        mSelect.add("a." + Database.activity.favourite_count);
        mSelect.add("a." + Database.activity.rating_average);

        mFrom.append(Database.activity.T + " a ");

        mOrderBy = Arrays.asList("a." + Database.activity.id);
    }

    public ActivityDataCursor query(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("" +
                " select " +
                "   " + TextUtils.join(", ", mSelect) +
                " from " +
                "   " + mFrom.toString());
        if (!mWhere.isEmpty()) {
            sb.append("" +
                    " where " + TextUtils.join(" and ", mWhere));
        }
        sb.append("" +
/*
                " group by " +
                "   a." + Database.activity.id + ", " +
                "   ad." + Database.activity_data.id + " " +
                " having " +
                "   ad." + Database.activity_data.id + " = max(admax." + Database.activity_data.id + ") " +
*/
                " order by " + TextUtils.join(", ", mOrderBy));
        if (mLimitResultLength > 0) {
            sb.append(" limit " + mLimitResultLength);
        }
        return new ActivityDataCursor(db.rawQuery(sb.toString(), mWhereConditions.toArray(new String[mWhereConditions.size()])));
    }

    public QueryBuilder addWhereFavourite(UserKey userKey) {
        mFrom.append("" +
                "   inner join " + Database.favourite_activity.T + " fa on fa." + Database.favourite_activity.activity_id + " = a." + Database.activity.id + " and fa." + Database.favourite_activity.user_id + " = " + userKey.getId());
        return this;
    }

    public QueryBuilder addWhereActivities(ActivityKey... activityKeys) {
        return addWhere("a." + Database.activity.id + " IN (" + TextUtils.join(",", activityKeys) + ")");
    }

    public QueryBuilder addWhereIsFeatured() {
        return addWhere("a." + Database.activity.featured + " = 1");
    }

    public QueryBuilder addWhereText(String text) {
        return addWhere("a." + Database.activity.name + " LIKE ?", "%" + text + "%");
    }

    public QueryBuilder addWhereAge(Range<Integer> range) {
        addWhereRange(range, Database.activity.age_min, Database.activity.age_max);
        return this;
    }

    public QueryBuilder addWhereTime(IntegerRange range) {
        addWhereRange(range, Database.activity.time_min, Database.activity.time_max);
        return this;
    }

    private void addWhereRange(Range<Integer> range, String minField, String maxField) {
        addWhere("not(" +
                String.valueOf(range.getMin()) + ">= a." + maxField +
                " or " +
                String.valueOf(range.getMax()) + "<= a." + minField +
                ")");
    }

    private QueryBuilder addWhere(String expr, String... params) {
        mWhere.add(expr);
        for (String param : params) {
            mWhereConditions.add(param);
        }
        return this;
    }

    public void addRandomlySelectedRows(int numberOfActivities) {
        mOrderBy = Collections.singletonList("RANDOM()");
        mLimitResultLength = numberOfActivities;
    }

    public void addWhereCategory(CategoryFilter filter) {
        addWhere("" +
                "exists(" +
                "   select " +
                "       activity_id " +
                "   from " +
                "       " + Database.activity_data_category.T + " adc " +
                "       inner join " +
                "       " + Database.category.T + " c " +
                "       on " +
                "       adc." + Database.activity_data_category.activity_data_id + " = a." + Database.activity.id +
                "       and" +
                "       adc." + Database.activity_data_category.category_id + " = c." + Database.category.id +
                "   where " +
                "       c." + Database.category.group_name + " = ?" +
                "       and" +
                "       c." + Database.category.name + " = ?" +
                ")",
                filter.getGroup(),
                filter.getName());
    }

    /**
     * The activities which have been marked as favourites by the most users (as reported by server API).
     */
    public void addWhereOverallFavourites(OverallFavouriteActivitiesFilter filter) {
        addWhere("" +
                "a." + Database.activity.id + " in " +
                "(" +
                "   select " + Database.activity.id +
                "   from " + Database.activity.T +
                "   order by " + Database.activity.favourite_count + " desc " +
                "   limit " + filter.getNumberOfActivities() +
                ")");
    }

    /**
     * Activities where the average rating of other users (as reported by the server API) is high enough OR
     * activities where the current user has set a high enough rating.
     */
    public void addWhereAverageRating(AverageRatingFilter filter) {
        mSelect.add("r." + Database.rating.rating);
        mFrom.append("" +
                "   left join " +
                "   " + Database.rating.T + " r " +
                "   on " +
                "   (" +
                "   r." + Database.rating.activity_id + " = a." + Database.activity.id + " " +
                "   and " +
                "   r." + Database.rating.user_id + " = " + CredentialsManager.getInstance(mContext).getCurrentUser().getId() +
                "   )");
        mWhere.add("" +
                "(" +
                "   a." + Database.activity.rating_average + " >= " + String.valueOf(filter.getLimit()) +
                "   or " +
                "   r." + Database.rating.rating + " >= " + String.valueOf(filter.getLimit()) +
                ")");
    }
}

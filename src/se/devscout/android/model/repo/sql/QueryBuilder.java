package se.devscout.android.model.repo.sql;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import se.devscout.android.model.IntegerRangePojo;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.Range;
import se.devscout.server.api.model.UserKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QueryBuilder {
    private List<String> mWhere = new ArrayList<String>();
    private List<String> mWhereConditions = new ArrayList<String>();
    private List<String> mSelect = new ArrayList<String>();
    private List<String> mOrderBy = new ArrayList<String>();
    private StringBuilder mFrom = new StringBuilder();
    private int mLimitResultLength = 0;

    public QueryBuilder() {
        mSelect.add("a." + Database.activity.owner_id);
        mSelect.add("ad." + Database.activity_data.id);
        mSelect.add("ad." + Database.activity_data.activity_id);
        mSelect.add("ad." + Database.activity_data.status);
        mSelect.add("ad." + Database.activity_data.name);
        mSelect.add("ad." + Database.activity_data.datetime_published);
        mSelect.add("ad." + Database.activity_data.datetime_created);
        mSelect.add("ad." + Database.activity_data.descr_material);
        mSelect.add("ad." + Database.activity_data.descr_introduction);
        mSelect.add("ad." + Database.activity_data.descr_prepare);
        mSelect.add("ad." + Database.activity_data.descr_activity);
        mSelect.add("ad." + Database.activity_data.descr_safety);
        mSelect.add("ad." + Database.activity_data.descr_notes);
        mSelect.add("ad." + Database.activity_data.age_min);
        mSelect.add("ad." + Database.activity_data.age_max);
        mSelect.add("ad." + Database.activity_data.participants_min);
        mSelect.add("ad." + Database.activity_data.participants_max);
        mSelect.add("ad." + Database.activity_data.time_min);
        mSelect.add("ad." + Database.activity_data.time_max);
        mSelect.add("ad." + Database.activity_data.featured);
        mSelect.add("ad." + Database.activity_data.author_id);
        mSelect.add("ad." + Database.activity_data.source_uri);
        mFrom.append("   " + Database.activity.T + " a " +
                "   inner join " + Database.activity_data.T + " admax on a." + Database.activity.id + " = admax." + Database.activity_data.activity_id + " " +
                "   inner join " + Database.activity_data.T + " ad on a." + Database.activity.id + " = ad." + Database.activity_data.activity_id + " ");
        mOrderBy = Arrays.asList("a." + Database.activity.id, "ad." + Database.activity_data.id);
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
                " group by " +
                "   a." + Database.activity.id + ", " +
                "   ad." + Database.activity_data.id + " " +
                " having " +
                "   ad." + Database.activity_data.id + " = max(admax." + Database.activity_data.id + ") " +
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

    public QueryBuilder addWhereActivity(ActivityKey activityKey) {
        return addWhere("a." + Database.activity.id + " = " + activityKey.getId());
    }

    public QueryBuilder addWhereIsFeatured() {
        return addWhere("ad." + Database.activity_data.featured + " = 1");
    }

    public QueryBuilder addWhereText(String text) {
        return addWhere("ad." + Database.activity_data.name + " LIKE ?", "%" + text + "%");
    }

    public QueryBuilder addWhereAge(Range<Integer> range) {
        addWhereRange(range, Database.activity_data.age_min, Database.activity_data.age_max);
        return this;
    }

    public QueryBuilder addWhereTime(IntegerRangePojo range) {
        addWhereRange(range, Database.activity_data.time_min, Database.activity_data.time_max);
        return this;
    }

    private void addWhereRange(Range<Integer> range, String minField, String maxField) {
        addWhere("not(" +
                String.valueOf(range.getMin()) + ">= ad." + maxField +
                " or " +
                String.valueOf(range.getMax()) + "<= ad." + minField +
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
}

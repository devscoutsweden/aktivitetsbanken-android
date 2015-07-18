package se.devscout.android.model;

import se.devscout.android.model.activityfilter.ActivityFilter;

import java.io.Serializable;

public interface SearchHistoryData extends Serializable {
    ActivityFilter getFilter();
}

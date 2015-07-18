package se.devscout.android.model;

import se.devscout.server.api.activityfilter.ActivityFilter;

import java.io.Serializable;

public interface SearchHistoryData extends Serializable {
    ActivityFilter getFilter();
}

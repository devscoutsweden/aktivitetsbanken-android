package se.devscout.server.api.model;

import se.devscout.server.api.ActivityFilter;

import java.io.Serializable;

public interface SearchHistoryData extends Serializable {
    ActivityFilter getFilter();
}

package se.devscout.server.api;

import se.devscout.server.api.model.ActivityProperties;

public interface ActivityFilter {
    boolean matches(ActivityProperties properties);
}

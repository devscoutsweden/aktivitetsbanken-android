package se.devscout.server.api;

import se.devscout.server.api.model.ActivityProperties;

import java.io.Serializable;

public interface ActivityFilter extends Serializable {
    boolean matches(ActivityProperties properties);
}

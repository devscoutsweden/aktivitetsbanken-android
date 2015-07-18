package se.devscout.server.api.activityfilter;

import se.devscout.server.api.model.ServerObjectIdentifier;

public interface ServerObjectIdentifiersFilter extends ActivityFilter {
    ServerObjectIdentifier[] getIdentifiers();
}

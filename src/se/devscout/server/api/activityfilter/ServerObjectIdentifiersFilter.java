package se.devscout.server.api.activityfilter;

import se.devscout.android.model.ServerObjectIdentifier;

public interface ServerObjectIdentifiersFilter extends ActivityFilter {
    ServerObjectIdentifier[] getIdentifiers();
}

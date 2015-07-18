package se.devscout.android.model.activityfilter;

import se.devscout.android.model.ServerObjectIdentifier;

public interface ServerObjectIdentifiersFilter extends ActivityFilter {
    ServerObjectIdentifier[] getIdentifiers();
}

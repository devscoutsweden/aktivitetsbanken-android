package se.devscout.android.model.repo.remote;

import se.devscout.android.model.ServerObjectIdentifier;
import se.devscout.server.api.activityfilter.BaseActivityFilterVisitor;
import se.devscout.server.api.activityfilter.ServerObjectIdentifiersFilter;

public class RemoteServerObjectIdentifiersFilter implements ServerObjectIdentifiersFilter {
    private ServerObjectIdentifier[] mIds;

    public RemoteServerObjectIdentifiersFilter(ServerObjectIdentifier[] ids) {
        mIds = ids;
    }

    @Override
    public ServerObjectIdentifier[] getIdentifiers() {
        return mIds;
    }

    @Override
    public <T> T visit(BaseActivityFilterVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

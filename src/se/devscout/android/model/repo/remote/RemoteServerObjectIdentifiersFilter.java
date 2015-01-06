package se.devscout.android.model.repo.remote;

import android.net.Uri;
import se.devscout.server.api.ActivityFilterVisitor;
import se.devscout.server.api.URIBuilderActivityFilterVisitor;
import se.devscout.server.api.activityfilter.ServerObjectIdentifiersFilter;
import se.devscout.server.api.model.ServerObjectIdentifier;

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
    public String toString(ActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public Uri toAPIRequest(URIBuilderActivityFilterVisitor visitor) {
        return visitor.visit(this);
    }
}

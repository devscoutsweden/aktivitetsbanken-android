package se.devscout.server.api;

import android.net.Uri;

import java.io.Serializable;

public interface ActivityFilter extends Serializable {
    String toString(ActivityFilterVisitor visitor);

    /**
     * Part of the Visitor pattern. Implementations should only invoke {@code visitor.visit(this)}.
     *
     * @param visitor
     * @return
     */
    Uri toAPIRequest(URIBuilderActivityFilterVisitor visitor);
}

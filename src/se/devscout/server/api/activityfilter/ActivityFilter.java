package se.devscout.server.api.activityfilter;

import java.io.Serializable;

public interface ActivityFilter extends Serializable {
    /**
     * Part of the Visitor pattern. Implementations should only invoke {@code visitor.visit(this)}.
     *
     * @param visitor
     * @return
     */
    <T> T visit(BaseActivityFilterVisitor<T> visitor);
}

package se.devscout.server.api.model;

import java.util.List;

public interface ActivityProperties {
    List<? extends ActivityRevision> getRevisions();

    Status getStatus();

    User getOwner();
}

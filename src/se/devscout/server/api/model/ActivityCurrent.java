package se.devscout.server.api.model;

public interface ActivityCurrent extends ActivityKey, ActivityRevisionProperties {
    Status getStatus();

    User getOwner();
}

package se.devscout.android.model;

public interface ActivityCurrent extends ActivityKey, ActivityRevisionProperties {
    Status getStatus();

    User getOwner();
}

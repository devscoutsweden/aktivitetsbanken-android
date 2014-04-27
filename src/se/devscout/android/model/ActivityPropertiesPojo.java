package se.devscout.android.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.ActivityRevision;
import se.devscout.server.api.model.Status;
import se.devscout.server.api.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonFilter("LocalActivity")
public class ActivityPropertiesPojo implements ActivityProperties, Serializable {
    private List<ActivityRevision> mRevisions = new ArrayList<ActivityRevision>();
    private User mOwner;

    protected ActivityPropertiesPojo(User owner) {
        mOwner = owner;
    }

    @Override
    public List<? extends ActivityRevision> getRevisions() {
        return mRevisions;
    }

/*
    private LocalActivityRevision getLatestRevisions() {
        return mRevisions.get(mRevisions.size() - 1);
    }
*/

    public void addRevisions(ActivityRevision revision) {
        mRevisions.add(revision);
    }

    @Override
    public Status getStatus() {
        return Status.PUBLISHED;
    }

    @Override
    public User getOwner() {
        return mOwner;
    }


    public void setOwner(User owner) {
        mOwner = owner;
    }

}

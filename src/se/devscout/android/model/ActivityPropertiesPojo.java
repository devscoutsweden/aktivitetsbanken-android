package se.devscout.android.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import se.devscout.server.api.model.ActivityProperties;
import se.devscout.server.api.model.Status;
import se.devscout.server.api.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonFilter("LocalActivity")
class ActivityPropertiesPojo implements ActivityProperties, Serializable {
    private List<LocalActivityRevision> mRevisions = new ArrayList<LocalActivityRevision>();
    private LocalUser mOwner;

    ActivityPropertiesPojo(LocalUser owner) {
        mOwner = owner;
    }

    @Override
    public List<LocalActivityRevision> getRevisions() {
        return mRevisions;
    }

/*
    private LocalActivityRevision getLatestRevisions() {
        return mRevisions.get(mRevisions.size() - 1);
    }
*/

    public void addRevisions(LocalActivityRevision revision) {
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


    public void setOwner(LocalUser owner) {
        mOwner = owner;
    }

}

package se.devscout.android.model.repo;

import com.fasterxml.jackson.annotation.JsonFilter;
import se.devscout.android.model.ActivityPropertiesPojo;
import se.devscout.server.api.model.Activity;
import se.devscout.server.api.model.User;

import java.io.Serializable;

@JsonFilter("LocalActivity")
public class LocalActivity extends ActivityPropertiesPojo implements Activity, Serializable, Comparable<LocalActivity> {
    public static long debugCounter;
//    private List<LocalActivityRevision> mRevisions = new ArrayList<LocalActivityRevision>();
    private LocalUser mOwner;
    private Long mId;

    public LocalActivity(LocalUser owner, Long id, int serverId, boolean publishable) {
        super(publishable, serverId, owner);
        mId = id;
    }

/*
    private LocalActivityRevision getLatestRevisions() {
        return mRevisions.get(mRevisions.size() - 1);
    }
*/

/*
    public void addRevisions(LocalActivityRevision revision) {
        mRevisions.add(revision);
    }
*/

    @Override
    public User getOwner() {
        return mOwner;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(LocalActivity localActivity) {
        return localActivity != null ? getName().compareTo(localActivity.getName()) : 0;
    }

    public void setOwner(LocalUser owner) {
        mOwner = owner;
    }

    @Override
    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }
/*
    public void setFeatured(boolean featured) {
        getLatestRevisions().setFeatured(featured);
    }
*/
}

package se.devscout.android.model.repo;

import com.fasterxml.jackson.annotation.JsonFilter;
import se.devscout.android.model.ActivityRevisionPropertiesPojo;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.ActivityRevision;

import java.io.Serializable;

@JsonFilter("LocalActivityRevision")
public class LocalActivityRevision extends ActivityRevisionPropertiesPojo implements ActivityRevision, Serializable {
    public static long debugCounter;
    private Long mId;

    public LocalActivityRevision(String name, boolean featured, ActivityKey activityKey, Long revisionId) {
        super(name, featured, activityKey);
        mId = revisionId;
    }

    @Override
    public Long getId() {
        return mId;
    }
}

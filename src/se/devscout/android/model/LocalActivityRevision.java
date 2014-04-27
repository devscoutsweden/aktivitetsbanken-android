package se.devscout.android.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.ActivityRevision;

import java.io.Serializable;

@JsonFilter("LocalActivityRevision")
class LocalActivityRevision extends ActivityRevisionPropertiesPojo implements ActivityRevision, Serializable {
    public static long debugCounter;
    private Long mId;

    LocalActivityRevision(String name, boolean featured, ActivityKey activityKey, Long revisionId) {
        super(name, featured, activityKey);
        mId = revisionId;
    }

    @Override
    public Long getId() {
        return mId;
    }
}

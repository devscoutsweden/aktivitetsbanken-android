package se.devscout.android.model.repo;

import com.fasterxml.jackson.annotation.JsonFilter;
import se.devscout.android.model.ActivityRevisionPropertiesPojo;
import se.devscout.server.api.model.ActivityKey;
import se.devscout.server.api.model.ActivityRevision;
import se.devscout.server.api.model.Reference;
import se.devscout.server.api.model.ReferenceType;

import java.io.Serializable;
import java.net.URI;

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

    public void addMediaItem(URI uri, String mimeType) {
        mMediaItems.add(new LocalMedia(uri, mimeType, LocalMedia.debugCounter++));
    }

    public void addCategory(String group, String name) {
        mCategories.add(new LocalCategory(group, name, LocalCategory.debugCounter++));
    }

    public void addReference(URI uri, ReferenceType type) {
        Reference reference = new LocalReference(LocalReference.debugCounter++, type, uri);
        mReferences.add(reference);
    }
}

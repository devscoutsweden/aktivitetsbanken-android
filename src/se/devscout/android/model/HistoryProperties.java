package se.devscout.android.model;

import java.io.Serializable;

public interface HistoryProperties<D extends Serializable> {
    UserKey getUser();

    HistoryType getType();

    D getData();
}

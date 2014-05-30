package se.devscout.server.api.model;

import java.io.Serializable;

public interface HistoryProperties<D extends Serializable> {
    UserKey getUser();

    HistoryType getType();

    D getData();
}

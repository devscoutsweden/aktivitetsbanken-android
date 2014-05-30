package se.devscout.server.api;

import java.io.Serializable;

public interface ActivityFilter extends Serializable {
    String toString(ActivityFilterVisitor visitor);
}

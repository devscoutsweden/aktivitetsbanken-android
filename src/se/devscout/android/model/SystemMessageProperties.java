package se.devscout.android.model;

import java.util.Date;

public interface SystemMessageProperties extends ServerObjectProperties {
    String getKey();

    String getValue();

    Date getValidFrom();

    Date getValidTo();
}

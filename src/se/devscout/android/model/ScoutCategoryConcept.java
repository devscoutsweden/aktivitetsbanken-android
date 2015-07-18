package se.devscout.android.model;

import java.util.UUID;

public enum ScoutCategoryConcept implements CategoryProperties {
    SCOUTSOWN("Andakt, stilla stund", UUID.fromString("38c62737-a233-43d7-9845-7fb4796a83c4")),
    CEREMONY("Ceremonier", UUID.fromString("f588bbf2-59bf-412e-b9ff-ecd0e8f0dab2")),
    DISCUSSION("Diskussion", UUID.fromString("e03865b4-6d06-4bdb-9fae-267fa3832b3a")),
    HIKE("Hajk", UUID.fromString("7a21f0fb-062d-4b85-89ba-18b0e857c099")),
    HANDICRAFT("Hantverk, pyssel", UUID.fromString("a68c1c82-fa6b-4e45-976a-f48ef5c62fbe")),
    CAMPFIRE("Lägerbål", UUID.fromString("62ea7a20-e456-451c-b8aa-87c380733814")),
    PLAYTIME("Lekar", UUID.fromString("ccea556c-e04e-4d7d-a8e5-dcb04ef74baf")),
    COOK("Matlagning", UUID.fromString("48952d54-9904-4d1e-8629-0407da805d3d")),
    OUTDOOR_INLAND("Friluftsliv land", UUID.fromString("7cd6cfab-d8c6-4921-ada7-d0eadf4f423a")),
    OUTDOOR_LAKE("Friluftsliv sjö", UUID.fromString("8dff3449-1ebb-4035-9fd0-7b60f70c83d4")),
    ART("Teater, musik, sång", UUID.fromString("1c661aa8-ee68-4b44-afd0-e787a0a678cb")),
    OUTDOOR("Utomhus", UUID.fromString("612af34f-448c-4031-9403-5586b2b626f4"));

    private final String name;
    private final UUID uuid;

    private ScoutCategoryConcept(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public long getServerId() {
        return -1;
    }

    @Override
    public long getServerRevisionId() {
        return -1;
    }

    @Override
    public boolean isPublishable() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

/*
    @Override
    public UUID getUniqueID() {
        return uuid;
    }
*/

    @Override
    public String getGroup() {
        return "scout-concept";
    }

    @Override
    public MediaKey getIconMediaKey() {
        return null;
    }

    @Override
    public Integer getActivitiesCount() {
        return null;
    }
}

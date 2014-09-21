package se.devscout.server.api.model;

import java.util.UUID;

public enum ScoutCategoryTrack implements CategoryProperties {
    ACTIVE_GROUP("Aktiv i gruppen", UUID.fromString("d62a22e5-373f-43bf-b9fd-925887c1d39f")),
    ACTIVE_COMMUNITY("Aktiv i samhället", UUID.fromString("e35c7598-10a1-4c93-b5c3-e2c6de41c15e")),
    PERSONAL_VIEWS("Egna värderingar", UUID.fromString("e563936a-f728-45d1-a052-bf6a2794b14d")),
    EXISTENCE("Existens", UUID.fromString("d17ef0ac-a2a2-4629-b041-a40755d24362")),
    CREATIVE_EXPRESSION("Fantasi och kreativt uttryck", UUID.fromString("32a2f900-4643-417c-af46-c625885ff8a2")),
    PHYSICAL_CHALLENGES("Fysiska utmaningar", UUID.fromString("be7465c1-18f8-4dd1-b762-bffa25740677")),
    UNDERSTANDING_SOCIETY("Förståelse för omvärlden", UUID.fromString("784e6b26-c62e-4d9d-9cba-6df81c6633ad")),
    ANALYTIC_THINKING("Kritiskt tänkande", UUID.fromString("151ad1b0-063f-4c12-9f68-af3c860aad12")),
    NATURE("Känsla för naturen", UUID.fromString("de7c0d8e-0d13-4204-a076-6a176027bc7d")),
    LEADERSHIP("Ledarskap", UUID.fromString("c69c7809-49f2-4299-bb8b-2abdb4b838c9")),
    PROBLEM_SOLVING("Problemlösning", UUID.fromString("1a47bd7b-df9b-4695-bd3b-9ee53bb7c308")),
    RELATIONS("Relationer", UUID.fromString("83cb1a1f-7bae-4ab0-8b3e-40312cc96abe")),
    SELF_ESTEEM("Självinsikt och självkänsla", UUID.fromString("b41ee9f7-9f7c-4657-939e-a05552572e02")),
    PHYSICAL_HEALTH("Ta hand om sin kropp", UUID.fromString("26fd5f95-5440-49c0-8b26-632a494e3fda"));

    private final String name;
    private final UUID uuid;

    private ScoutCategoryTrack(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public int getServerId() {
        return -1;
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
        return "scout-track";
    }
}

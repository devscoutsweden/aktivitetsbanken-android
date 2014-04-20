package se.devscout.server.api.model;

public enum ScoutAgeRange implements Range<Integer> {
    TRACKER("spårarscout", 8, 9),
    DISCOVERER("upptäckarscout", 10, 12),
    ADVENTURER("äventyrsscout", 12, 15),
    CHALLENGER("utmanarscout", 15, 18),
    ROVER("roverscout", 19, 25);

    String name;
    int min;
    int max;

    private ScoutAgeRange(String name, int min, int max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer getMin() {
        return min;
    }

    @Override
    public Integer getMax() {
        return max;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

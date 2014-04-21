package se.devscout.server.api.model;

public enum ScoutAgeRange implements Range<Integer> {
    TRACKER(8, 9),
    DISCOVERER(10, 12),
    ADVENTURER(12, 15),
    CHALLENGER(15, 18),
    ROVER(19, 25);

    int min;
    int max;

    private ScoutAgeRange(int min, int max) {
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
}

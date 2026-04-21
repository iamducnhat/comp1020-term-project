package model.beverage;

/**
 * Enum representing drink sizes.
 * Each size has a price multiplier applied to the base price.
 */
public enum Size {
    S(1.0),
    M(1.3),
    L(1.6);

    private final double multiplier;

    Size(double multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Returns the price multiplier for this size.
     * @return the multiplier value
     */
    public double getMultiplier() {
        return multiplier;
    }
}

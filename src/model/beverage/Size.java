package model.beverage;

/**
 * Drink size options — used across all Beverage subclasses.
 * Affects base price calculation.
 */
public enum Size {
    S("Small",  0),
    M("Medium", 5_000),
    L("Large",  10_000);

    private final String label;
    private final double extraPrice;

    Size(String label, double extraPrice) {
        this.label      = label;
        this.extraPrice = extraPrice;
    }

    public String getLabel()       { return label; }
    public double getExtraPrice()  { return extraPrice; }

    @Override
    public String toString() { return label; }
}

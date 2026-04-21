package model.beverage;

/**
 * Concrete beverage representing a coffee drink.
 * Inherits from {@link Beverage} (Inheritance).
 *
 * Coffee has a 10% surcharge on top of the size-adjusted price
 * to reflect premium bean costs.
 */
public class Coffee extends Beverage {

    private static final double COFFEE_SURCHARGE = 0.10; // 10% premium

    /**
     * Constructs a Coffee beverage.
     *
     * @param name      the coffee variant name (e.g. "Americano", "Latte")
     * @param size      the drink size
     * @param basePrice the base price before adjustments
     */
    public Coffee(String name, Size size, double basePrice) {
        super(name, size, basePrice);
    }

    /**
     * Calculates the price: basePrice × sizeMultiplier × (1 + surcharge).
     * Polymorphic implementation specific to Coffee.
     *
     * @return the final coffee price
     */
    @Override
    public double calculatePrice() {
        return getBasePrice() * getSize().getMultiplier() * (1 + COFFEE_SURCHARGE);
    }

    @Override
    public String getDescription() {
        return "Coffee: " + super.getDescription();
    }
}

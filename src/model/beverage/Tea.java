package model.beverage;

/**
 * Concrete beverage representing a tea drink.
 * Inherits from {@link Beverage} (Inheritance).
 *
 * Tea uses a straightforward pricing model:
 * basePrice × sizeMultiplier (no surcharge).
 */
public class Tea extends Beverage {

    /**
     * Constructs a Tea beverage.
     *
     * @param name      the tea variant name (e.g. "Green Tea", "Oolong")
     * @param size      the drink size
     * @param basePrice the base price before size adjustment
     */
    public Tea(String name, Size size, double basePrice) {
        super(name, size, basePrice);
    }

    /**
     * Calculates the price: basePrice × sizeMultiplier.
     * Polymorphic implementation specific to Tea.
     *
     * @return the final tea price
     */
    @Override
    public double calculatePrice() {
        return getBasePrice() * getSize().getMultiplier();
    }

    @Override
    public String getDescription() {
        return "Tea: " + super.getDescription();
    }
}

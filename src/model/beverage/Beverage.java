package model.beverage;

import java.util.Collections;
import java.util.Map;

/**
 * Abstract base for all drinks (Coffee, Tea, and decorated variants).
 *
 * OOP: Abstraction — defines common contract for the whole beverage hierarchy.
 * Decorator Pattern root — ToppingDecorator also extends this class.
 */
public abstract class Beverage {

    protected String name;
    protected Size   size;

    /** Calculate total price including size and any decorators. */
    public abstract double calculatePrice();

    /** Human-readable description (e.g. "Latte (M) + Milk Foam"). */
    public abstract String getDescription();

    /** Ingredients consumed when this beverage is processed. */
    public Map<String, Integer> getIngredientRequirements() {
        return Collections.emptyMap();
    }

    public String getName() { return name; }
    public Size   getSize() { return size; }

    @Override
    public String toString() {
        return String.format("%s — %.0f VND", getDescription(), calculatePrice());
    }
}

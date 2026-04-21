package model.addon;

import model.beverage.Beverage;
import model.beverage.Size;

/**
 * Abstract decorator for adding toppings to a {@link Beverage}.
 * Implements the Decorator Pattern: wraps an existing Beverage
 * and extends its behavior without modifying the original class.
 *
 * Since ToppingDecorator IS-A Beverage, it can be used anywhere
 * a Beverage is expected (Liskov Substitution Principle).
 */
public abstract class ToppingDecorator extends Beverage {

    private Beverage decoratedBeverage;

    /**
     * Constructs a ToppingDecorator wrapping the given beverage.
     *
     * @param beverage the beverage to decorate
     */
    public ToppingDecorator(Beverage beverage) {
        super(beverage.getName(), beverage.getSize(), beverage.getBasePrice());
        this.decoratedBeverage = beverage;
    }

    /**
     * Returns the wrapped beverage instance.
     *
     * @return the decorated beverage
     */
    public Beverage getDecoratedBeverage() {
        return decoratedBeverage;
    }

    @Override
    public abstract double calculatePrice();

    @Override
    public abstract String getDescription();
}

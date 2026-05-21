package model.addon;

import model.beverage.Beverage;
import model.beverage.Size;

/**
 * Abstract Decorator — wraps any Beverage and delegates core methods.
 *
 * Design Pattern: Decorator Pattern (GoF)
 * Allows adding toppings dynamically without modifying Beverage subclasses.
 */
public abstract class ToppingDecorator extends Beverage {

    protected final Beverage beverage;  // wrapped component

    protected ToppingDecorator(Beverage beverage) {
        this.beverage = beverage;
        this.name     = beverage.getName();
        this.size     = beverage.getSize();
    }

    /** Subclasses add their own extra cost on top of the wrapped price. */
    @Override
    public abstract double calculatePrice();

    @Override
    public abstract String getDescription();

    public Beverage getWrapped() { return beverage; }
}

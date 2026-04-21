package model.beverage;

/**
 * Abstract base class for all beverages in the coffee shop.
 * Demonstrates Abstraction and Encapsulation (OOP principles).
 *
 * Subclasses must implement {@link #calculatePrice()} to define
 * their own pricing logic (Polymorphism).
 */
public abstract class Beverage {

    private String name;
    private Size size;
    private double basePrice;

    /**
     * Constructs a Beverage with the given name, size, and base price.
     *
     * @param name      the display name of the beverage
     * @param size      the size (S, M, or L)
     * @param basePrice the base price before size multiplier
     */
    public Beverage(String name, Size size, double basePrice) {
        this.name = name;
        this.size = size;
        this.basePrice = basePrice;
    }

    /**
     * Calculates the final price of this beverage.
     * Each subclass implements its own pricing logic (Polymorphism).
     *
     * @return the calculated price
     */
    public abstract double calculatePrice();

    /**
     * Returns a human-readable description of this beverage.
     *
     * @return description string
     */
    public String getDescription() {
        return name + " (" + size + ")";
    }

    // ── Getters & Setters (Encapsulation) ──────────────────────────

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public String toString() {
        return getDescription() + " - $" + String.format("%.2f", calculatePrice());
    }
}

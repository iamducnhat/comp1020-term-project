package model.addon;

import model.beverage.Beverage;

/**
 * Concrete decorator that adds a topping to a beverage.
 * Each Topping has a name and an additional price that is
 * added to the decorated beverage's price.
 *
 * Multiple toppings can be stacked by wrapping decorators:
 *   new Topping(new Topping(baseBeverage, "Boba", 0.75), "Cream", 0.50)
 */
public class Topping extends ToppingDecorator {

    private String toppingName;
    private double toppingPrice;

    /**
     * Constructs a Topping decorator.
     *
     * @param beverage     the beverage to add a topping to
     * @param toppingName  the name of the topping (e.g. "Boba", "Whipped Cream")
     * @param toppingPrice the extra cost of this topping
     */
    public Topping(Beverage beverage, String toppingName, double toppingPrice) {
        super(beverage);
        this.toppingName = toppingName;
        this.toppingPrice = toppingPrice;
    }

    /**
     * Price = decorated beverage price + topping price.
     *
     * @return the total price including this topping
     */
    @Override
    public double calculatePrice() {
        return getDecoratedBeverage().calculatePrice() + toppingPrice;
    }

    /**
     * Description appends the topping info to the decorated beverage's description.
     *
     * @return combined description
     */
    @Override
    public String getDescription() {
        return getDecoratedBeverage().getDescription() + " + " + toppingName;
    }

    // ── Getters & Setters ──────────────────────────────────────────

    public String getToppingName() {
        return toppingName;
    }

    public void setToppingName(String toppingName) {
        this.toppingName = toppingName;
    }

    public double getToppingPrice() {
        return toppingPrice;
    }

    public void setToppingPrice(double toppingPrice) {
        this.toppingPrice = toppingPrice;
    }
}

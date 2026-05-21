package model.addon;

import model.beverage.Beverage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Concrete Decorator — represents one topping added to a drink.
 *
 * Usage example:
 *   Beverage b = new Coffee(CoffeeType.LATTE, Size.M);
 *   b = new Topping(b, ToppingType.MILK_FOAM);
 *   b = new Topping(b, ToppingType.CARAMEL_SYRUP);
 *   // b.calculatePrice() => 30000 + 5000 (M) + 8000 + 10000
 */
public class Topping extends ToppingDecorator {

    public enum ToppingType {
        MILK_FOAM("Milk Foam",         8_000),
        CARAMEL_SYRUP("Caramel Syrup", 10_000),
        WHIPPED_CREAM("Whipped Cream", 12_000),
        PEARL("Pearl (Boba)",          10_000),
        COCONUT_JELLY("Coconut Jelly", 9_000),
        EXTRA_SHOT("Extra Espresso",   15_000);

        private final String label;
        private final double price;

        ToppingType(String label, double price) {
            this.label = label;
            this.price = price;
        }

        public String getLabel() { return label; }
        public double getPrice() { return price; }
    }

    private final ToppingType toppingType;

    public Topping(Beverage beverage, ToppingType toppingType) {
        super(beverage);
        this.toppingType = toppingType;
    }

    @Override
    public double calculatePrice() {
        return beverage.calculatePrice() + toppingType.getPrice();
    }

    @Override
    public String getDescription() {
        return beverage.getDescription() + " + " + toppingType.getLabel();
    }

    @Override
    public Map<String, Integer> getIngredientRequirements() {
        Map<String, Integer> ingredients = new LinkedHashMap<>(beverage.getIngredientRequirements());
        String ingredientName = switch (toppingType) {
            case MILK_FOAM -> "Whole Milk";
            case CARAMEL_SYRUP -> "Caramel Syrup";
            case WHIPPED_CREAM -> "Whipped Cream";
            case PEARL -> "Boba Pearls";
            case COCONUT_JELLY -> "Coconut Jelly";
            case EXTRA_SHOT -> "Espresso Beans";
        };
        int amount = switch (toppingType) {
            case MILK_FOAM -> 60;
            case CARAMEL_SYRUP -> 25;
            case WHIPPED_CREAM -> 30;
            case PEARL, COCONUT_JELLY -> 40;
            case EXTRA_SHOT -> 10;
        };
        ingredients.merge(ingredientName, amount, Integer::sum);
        return ingredients;
    }

    public ToppingType getToppingType() { return toppingType; }
}

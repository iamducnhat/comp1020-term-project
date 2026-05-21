package model.beverage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Concrete coffee drink.
 * OOP: Inheritance from Beverage, Polymorphism via calculatePrice().
 *
 * Base prices (S):
 *   Espresso   25,000
 *   Latte      30,000
 *   Cappuccino 32,000
 *   Americano  28,000
 */
public class Coffee extends Beverage {

    public enum CoffeeType {
        ESPRESSO("Espresso",   25_000),
        LATTE("Latte",         30_000),
        CAPPUCCINO("Cappuccino", 32_000),
        AMERICANO("Americano", 28_000);

        private final String label;
        private final double basePrice;

        CoffeeType(String label, double basePrice) {
            this.label     = label;
            this.basePrice = basePrice;
        }

        public String getLabel()      { return label; }
        public double getBasePrice()  { return basePrice; }
    }

    private final CoffeeType type;

    public Coffee(CoffeeType type, Size size) {
        this.type = type;
        this.size = size;
        this.name = type.getLabel();
    }

    @Override
    public double calculatePrice() {
        return type.getBasePrice() + size.getExtraPrice();
    }

    @Override
    public String getDescription() {
        return name + " (" + size.getLabel() + ")";
    }

    @Override
    public Map<String, Integer> getIngredientRequirements() {
        int factor = switch (size) {
            case S -> 1;
            case M -> 2;
            case L -> 3;
        };

        Map<String, Integer> ingredients = new LinkedHashMap<>();
        ingredients.put("Espresso Beans", 18 * factor);
        if (type == CoffeeType.LATTE || type == CoffeeType.CAPPUCCINO) {
            ingredients.put("Whole Milk", 160 * factor);
        }
        if (type == CoffeeType.CAPPUCCINO) {
            ingredients.put("Whipped Cream", 20 * factor);
        }
        ingredients.put("Sugar", 5 * factor);
        return ingredients;
    }

    public CoffeeType getType() { return type; }
}

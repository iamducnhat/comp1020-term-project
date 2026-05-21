package model.beverage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Concrete tea drink.
 * OOP: Inheritance from Beverage.
 */
public class Tea extends Beverage {

    public enum TeaType {
        GREEN_TEA("Green Tea",   20_000),
        BLACK_TEA("Black Tea",   22_000),
        OOLONG("Oolong Tea",     25_000),
        JASMINE("Jasmine Tea",   23_000);

        private final String label;
        private final double basePrice;

        TeaType(String label, double basePrice) {
            this.label     = label;
            this.basePrice = basePrice;
        }

        public String getLabel()      { return label; }
        public double getBasePrice()  { return basePrice; }
    }

    private final TeaType type;

    public Tea(TeaType type, Size size) {
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
        ingredients.put("Tea Leaves", 8 * factor);
        ingredients.put("Sugar", 4 * factor);
        return ingredients;
    }

    public TeaType getType() { return type; }
}

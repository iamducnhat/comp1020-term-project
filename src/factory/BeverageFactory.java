package factory;

import model.beverage.Beverage;
import model.beverage.Coffee;
import model.beverage.Coffee.CoffeeType;
import model.beverage.Size;
import model.beverage.Tea;
import model.beverage.Tea.TeaType;

/**
 * Factory Pattern — creates Beverage objects without exposing subclass details.
 *
 * Design Pattern: Factory Method / Simple Factory
 * Why: The view layer only needs to call BeverageFactory.create(...),
 *      without knowing whether it gets a Coffee or Tea back.
 */
public class BeverageFactory {

    public enum BeverageCategory { COFFEE, TEA }

    // ── Main factory methods ─────────────────────────────────────────────────

    /**
     * Create a Coffee by type name string (case-insensitive).
     * Used when reading from UI text fields or combo boxes.
     *
     * @param typeName  e.g. "LATTE", "Espresso"
     * @param sizeLabel e.g. "S", "M", "L"
     */
    public static Beverage createCoffee(String typeName, String sizeLabel) {
        CoffeeType type = parseCoffeeType(typeName);
        Size       size = parseSize(sizeLabel);
        return new Coffee(type, size);
    }

    public static Beverage createCoffee(CoffeeType type, Size size) {
        return new Coffee(type, size);
    }

    /**
     * Create a Tea by type name string (case-insensitive).
     */
    public static Beverage createTea(String typeName, String sizeLabel) {
        TeaType type = parseTeaType(typeName);
        Size    size = parseSize(sizeLabel);
        return new Tea(type, size);
    }

    public static Beverage createTea(TeaType type, Size size) {
        return new Tea(type, size);
    }

    /**
     * Generic factory — choose category then type and size by string.
     *
     * @param category "COFFEE" or "TEA"
     * @param typeName specific drink type
     * @param sizeLabel "S" / "M" / "L"
     */
    public static Beverage create(String category, String typeName, String sizeLabel) {
        BeverageCategory cat;
        try {
            cat = BeverageCategory.valueOf(category.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Unknown category: " + category + ". Use COFFEE or TEA.");
        }
        return switch (cat) {
            case COFFEE -> createCoffee(typeName, sizeLabel);
            case TEA    -> createTea(typeName, sizeLabel);
        };
    }

    // ── Parsers (with friendly error messages) ───────────────────────────────

    private static Size parseSize(String label) {
        for (Size s : Size.values()) {
            if (s.name().equalsIgnoreCase(label.trim())) return s;
        }
        throw new IllegalArgumentException(
            "Unknown size: \"" + label + "\". Valid values: S, M, L");
    }

    private static CoffeeType parseCoffeeType(String name) {
        for (CoffeeType t : CoffeeType.values()) {
            if (t.name().equalsIgnoreCase(name.trim())
             || t.getLabel().equalsIgnoreCase(name.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException(
            "Unknown coffee type: \"" + name + "\"");
    }

    private static TeaType parseTeaType(String name) {
        for (TeaType t : TeaType.values()) {
            if (t.name().equalsIgnoreCase(name.trim())
             || t.getLabel().equalsIgnoreCase(name.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException(
            "Unknown tea type: \"" + name + "\"");
    }
}

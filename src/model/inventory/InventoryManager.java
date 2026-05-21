package model.inventory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all café ingredients.
 *
 * Design Pattern: Singleton — one global inventory for the whole application.
 * Data Structure: HashMap<String, Ingredient> — O(1) lookup by ingredient name.
 */
public class InventoryManager {

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static InventoryManager instance;

    private InventoryManager() {
        seedDefaultIngredients();
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    // ── Storage ───────────────────────────────────────────────────────────────
    private final Map<String, Ingredient> inventory = new HashMap<>();

    // ── Seed data ─────────────────────────────────────────────────────────────
    private void seedDefaultIngredients() {
        addIngredient(new Ingredient("Espresso Beans", 5000, "g",   500));
        addIngredient(new Ingredient("Whole Milk",     10000, "ml", 1000));
        addIngredient(new Ingredient("Sugar",          3000, "g",   300));
        addIngredient(new Ingredient("Tea Leaves",     2000, "g",   200));
        addIngredient(new Ingredient("Caramel Syrup",  1500, "ml",  200));
        addIngredient(new Ingredient("Whipped Cream",  1000, "ml",  150));
        addIngredient(new Ingredient("Boba Pearls",    2000, "g",   200));
        addIngredient(new Ingredient("Coconut Jelly",  1500, "g",   150));
    }

    // ── CRUD operations ───────────────────────────────────────────────────────
    public void addIngredient(Ingredient ingredient) {
        inventory.put(ingredient.getName(), ingredient);
    }

    public Ingredient getIngredient(String name) {
        return inventory.get(name);   // null if not found
    }

    public boolean restock(String name, int amount) {
        Ingredient ing = inventory.get(name);
        if (ing == null) return false;
        ing.restock(amount);
        return true;
    }

    public boolean consume(String name, int amount) {
        Ingredient ing = inventory.get(name);
        if (ing == null) return false;
        return ing.consume(amount);
    }

    public boolean removeIngredient(String name) {
        return inventory.remove(name) != null;
    }

    // ── Query ─────────────────────────────────────────────────────────────────
    public Collection<Ingredient> getAllIngredients() {
        return Collections.unmodifiableCollection(inventory.values());
    }

    public boolean hasIngredient(String name) {
        return inventory.containsKey(name);
    }

    /** Returns all ingredients that are at or below their low-stock threshold. */
    public Collection<Ingredient> getLowStockIngredients() {
        return inventory.values().stream()
            .filter(Ingredient::isLowStock)
            .toList();
    }
}

package controller;

import model.inventory.Ingredient;
import model.inventory.InventoryManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * InventoryController — thin controller delegating to InventoryManager Singleton.
 *
 * Design Pattern: Singleton delegation.
 * MVC: Sits between InventoryPanel (view) and InventoryManager (model).
 */
public class InventoryController {

    // Singleton — shared across the whole application
    private final InventoryManager manager = InventoryManager.getInstance();

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public void addIngredient(String name, int qty, String unit, int lowStockThreshold) {
        if (manager.hasIngredient(name))
            throw new IllegalArgumentException("Ingredient '" + name + "' already exists.");
        manager.addIngredient(new Ingredient(name, qty, unit, lowStockThreshold));
    }

    public boolean restock(String name, int amount) {
        return manager.restock(name, amount);
    }

    /**
     * Consume stock when a drink is made.
     * @return false if insufficient stock.
     */
    public boolean consume(String name, int amount) {
        return manager.consume(name, amount);
    }

    public boolean removeIngredient(String name) {
        return manager.removeIngredient(name);
    }

    public boolean hasEnoughStock(Map<String, Integer> requirements) {
        return getMissingStock(requirements).isEmpty();
    }

    public List<String> getMissingStock(Map<String, Integer> requirements) {
        List<String> missing = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
            Ingredient ingredient = manager.getIngredient(entry.getKey());
            int available = ingredient == null ? 0 : ingredient.getQuantity();
            if (available < entry.getValue()) {
                missing.add(String.format("%s needs %d, has %d", entry.getKey(), entry.getValue(), available));
            }
        }
        return missing;
    }

    public void consumeIngredients(Map<String, Integer> requirements) {
        List<String> missing = getMissingStock(requirements);
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Insufficient inventory: " + String.join("; ", missing));
        }
        for (Map.Entry<String, Integer> entry : requirements.entrySet()) {
            manager.consume(entry.getKey(), entry.getValue());
        }
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public Ingredient getIngredient(String name) {
        return manager.getIngredient(name);
    }

    public Collection<Ingredient> getAllIngredients() {
        return manager.getAllIngredients();
    }

    public Collection<Ingredient> getLowStockAlerts() {
        return manager.getLowStockIngredients();
    }

    public boolean hasLowStock() {
        return !manager.getLowStockIngredients().isEmpty();
    }
}

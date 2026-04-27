import java.util.HashMap;

public class InventoryManager {
    private static InventoryManager instance;
    private HashMap<String, Integer> inventory;

    private InventoryManager() {
        inventory = new HashMap<>();
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager(); // Lazy initialization
        }
        return instance;
    }

    // Add new ingredient
    public void addIngredient(String name, int quantity) {
        inventory.put(name, inventory.getOrDefault(name, 0) + quantity);
    }

    // Update exact quantity
    public void updateIngredient(String name, int quantity) {
        inventory.put(name, quantity);
    }

    // Remove ingredient
    public void removeIngredient(String name) {
        inventory.remove(name);
    }

    // Check availability
    public boolean isAvailable(String name, int required) {
        return inventory.getOrDefault(name, 0) >= required;
    }

    // Deduct ingredient after order
    public boolean useIngredient(String name, int amount) {
        if (!isAvailable(name, amount)) {
            return false;
        }
        inventory.put(name, inventory.get(name) - amount);
        return true;
    }

    public HashMap<String, Integer> getInventory() {
        return inventory;
    }
}
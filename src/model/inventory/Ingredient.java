package model.inventory;

/**
 * Represents a single ingredient tracked by InventoryManager.
 *
 * Stored inside InventoryManager's HashMap<String, Ingredient>.
 */
public class Ingredient {

    private final String name;
    private       int    quantity;
    private final String unit;       // "ml", "g", "pcs", etc.
    private       int    lowStockThreshold;

    public Ingredient(String name, int quantity, String unit, int lowStockThreshold) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (quantity < 0)                   throw new IllegalArgumentException("Quantity >= 0");

        this.name               = name.trim();
        this.quantity           = quantity;
        this.unit               = unit;
        this.lowStockThreshold  = lowStockThreshold;
    }

    // ── Stock operations ──────────────────────────────────────────────────────
    public void restock(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Restock amount must be > 0");
        quantity += amount;
    }

    /**
     * @return true if deduction succeeded, false if not enough stock.
     */
    public boolean consume(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Consume amount must be > 0");
        if (amount > quantity) return false;
        quantity -= amount;
        return true;
    }

    public boolean isLowStock() { return quantity <= lowStockThreshold; }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public String getName()              { return name; }
    public int    getQuantity()          { return quantity; }
    public String getUnit()              { return unit; }
    public int    getLowStockThreshold() { return lowStockThreshold; }

    public void setLowStockThreshold(int t) { this.lowStockThreshold = t; }

    @Override
    public String toString() {
        return String.format(
            "%-20s %4d %-4s%s",
            name, quantity, unit, isLowStock() ? " LOW" : ""
        );
    }
}

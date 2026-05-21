package model.order;

/**
 * Lifecycle states for an Order.
 * Used by OrderController and the view layer to reflect current state.
 */
public enum OrderStatus {
    PENDING    ("Pending"),
    PROCESSING ("Processing"),
    COMPLETED  ("Completed"),
    CANCELLED  ("Cancelled");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}

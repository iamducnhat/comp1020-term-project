package model.voucher;

/**
 * Discount voucher redeemable at checkout.
 *
 * Stored in VoucherController's HashMap<String, Voucher> for O(1) lookup.
 */
public class Voucher {

    public enum VoucherType {
        PERCENT ("Percentage Discount"),
        FIXED   ("Fixed Amount Discount");

        private final String label;
        VoucherType(String label) { this.label = label; }
        public String getLabel()  { return label; }
    }

    private final String      code;
    private final VoucherType type;
    private final double      value;       // % or VND
    private final double      minOrder;    // minimum order value to apply
    private       boolean     used;

    public Voucher(String code, VoucherType type, double value, double minOrder) {
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("Voucher code required");
        if (value <= 0) throw new IllegalArgumentException("Discount value must be > 0");
        if (type == VoucherType.PERCENT && value > 100)
            throw new IllegalArgumentException("Percent discount cannot exceed 100%");

        this.code     = code.toUpperCase().trim();
        this.type     = type;
        this.value    = value;
        this.minOrder = minOrder;
        this.used     = false;
    }

    /**
     * Calculate discount amount for a given subtotal.
     * Returns 0 if voucher is used, invalid, or subtotal < minOrder.
     */
    public double calculateDiscount(double subtotal) {
        if (used || subtotal < minOrder) return 0;
        return switch (type) {
            case PERCENT -> subtotal * (value / 100.0);
            case FIXED   -> Math.min(value, subtotal); // can't discount more than subtotal
        };
    }

    /** Mark voucher as used — irreversible. */
    public void markUsed() { used = true; }

    public boolean isValid(double orderTotal) {
        return !used && orderTotal >= minOrder;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String      getCode()     { return code; }
    public VoucherType getType()     { return type; }
    public double      getValue()    { return value; }
    public double      getMinOrder() { return minOrder; }
    public boolean     isUsed()      { return used; }

    @Override
    public String toString() {
        String discount = (type == VoucherType.PERCENT)
            ? value + "% off"
            : String.format("%.0f VND off", value);
        return String.format(
            "[%s] %s | Min order: %.0f VND | %s",
            code, discount, minOrder, used ? "USED" : "Available"
        );
    }
}

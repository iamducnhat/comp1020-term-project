package model.customer;

/**
 * Represents a café customer.
 *
 * OOP: Encapsulation — loyalty points are managed exclusively through
 * addPoints() / redeemPoints() to prevent invalid states.
 *
 * Identified by phone number (used as HashMap key in CustomerController).
 */
public class Customer {

    private final String phone;     // unique identifier
    private       String name;
    private       int    loyaltyPoints;
    private       String email;     // optional

    public Customer(String name, String phone) {
        if (phone == null || phone.isBlank())
            throw new IllegalArgumentException("Phone number is required");
        if (name  == null || name.isBlank())
            throw new IllegalArgumentException("Name is required");

        this.name          = name.trim();
        this.phone         = phone.trim();
        this.loyaltyPoints = 0;
    }

    // ── Points management ────────────────────────────────────────────────────
    public void addPoints(int pts) {
        if (pts < 0) throw new IllegalArgumentException("Points to add must be >= 0");
        loyaltyPoints += pts;
    }

    /**
     * @return true if redemption was successful, false if not enough points.
     */
    public boolean redeemPoints(int pts) {
        if (pts < 0) throw new IllegalArgumentException("Points to redeem must be >= 0");
        if (pts > loyaltyPoints) return false;
        loyaltyPoints -= pts;
        return true;
    }

    public boolean isVip() {
        return loyaltyPoints >= 100; // mirrors Order.VIP_THRESHOLD
    }

    // ── Getters / Setters ────────────────────────────────────────────────────
    public String getPhone()         { return phone; }
    public String getName()          { return name; }
    public int    getLoyaltyPoints() { return loyaltyPoints; }
    public String getEmail()         { return email; }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        this.name = name.trim();
    }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return String.format(
            "[%s] %s | %d pts%s",
            phone, name, loyaltyPoints, isVip() ? " VIP" : ""
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer c)) return false;
        return phone.equals(c.phone);
    }

    @Override
    public int hashCode() { return phone.hashCode(); }
}

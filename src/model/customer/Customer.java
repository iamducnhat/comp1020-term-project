package model.customer;

/**
 * Represents a customer in the coffee POS system with loyalty points tracking.
 */
public class Customer {
    private String phoneNumber;
    private String name;
    private int loyaltyPoints;

    /**
     * Constructs a Customer with the specified phone number and name.
     * @param phoneNumber the customer's phone number (unique identifier)
     * @param name the customer's name
     */
    public Customer(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.loyaltyPoints = 0;
    }

    /**
     * Constructs a Customer with phone number, name, and initial loyalty points.
     * @param phoneNumber the customer's phone number (unique identifier)
     * @param name the customer's name
     * @param loyaltyPoints the initial loyalty points
     */
    public Customer(String phoneNumber, String name, int loyaltyPoints) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.loyaltyPoints = loyaltyPoints;
    }

    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    /**
     * Adds loyalty points to the customer's account.
     * @param points the number of points to add
     */
    public void addPoints(int points) {
        if (points > 0) {
            this.loyaltyPoints += points;
        }
    }

    /**
     * Redeems (deducts) loyalty points from the customer's account.
     * @param points the number of points to redeem
     * @return true if redemption was successful, false if insufficient points
     */
    public boolean redeemPoints(int points) {
        if (points > 0 && this.loyaltyPoints >= points) {
            this.loyaltyPoints -= points;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                '}';
    }
}

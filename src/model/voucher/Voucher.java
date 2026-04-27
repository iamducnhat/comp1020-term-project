package model.voucher;

import java.time.LocalDate;

/**
 * Strategy interface for calculating discount amounts.
 * Implements Strategy pattern for different discount types.
 */
interface DiscountStrategy {
    /**
     * Calculates the discount amount based on the original price.
     * @param originalPrice the original price before discount
     * @param value the discount value (percentage or fixed amount)
     * @return the discount amount
     */
    double calculateDiscount(double originalPrice, double value);

    /**
     * Gets the discount type name.
     * @return the type of discount
     */
    String getType();
}

/**
 * Percentage-based discount strategy.
 * Calculates discount as a percentage of the original price.
 */
class PercentageDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double originalPrice, double value) {
        if (value < 0 || value > 100) {
            return 0;
        }
        return originalPrice * (value / 100);
    }

    @Override
    public String getType() {
        return "PERCENTAGE";
    }
}

/**
 * Fixed-amount discount strategy.
 * Calculates discount as a fixed amount off the original price.
 */
class FixedDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double originalPrice, double value) {
        if (value < 0) {
            return 0;
        }
        // Discount cannot exceed original price
        return Math.min(value, originalPrice);
    }

    @Override
    public String getType() {
        return "FIXED";
    }
}

/**
 * Represents a voucher/coupon with discount capabilities.
 * Uses Strategy pattern for different discount types.
 */
public class Voucher {
    private String code;
    private DiscountStrategy discountStrategy;
    private double value;
    private LocalDate expiry;
    private boolean active;

    /**
     * Constructs a Voucher with the specified parameters.
     * @param code the unique voucher code
     * @param discountStrategy the discount strategy (Percentage or Fixed)
     * @param value the discount value (percentage or fixed amount)
     * @param expiry the expiry date of the voucher
     */
    public Voucher(String code, DiscountStrategy discountStrategy, double value, LocalDate expiry) {
        this.code = code;
        this.discountStrategy = discountStrategy;
        this.value = value;
        this.expiry = expiry;
        this.active = true;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DiscountStrategy getDiscountStrategy() {
        return discountStrategy;
    }

    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDate getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDate expiry) {
        this.expiry = expiry;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Checks if the voucher is still valid (not expired and active).
     * @return true if voucher is valid, false otherwise
     */
    public boolean isValid() {
        return active && expiry.isAfter(LocalDate.now());
    }

    /**
     * Calculates the discount amount for the given original price.
     * @param originalPrice the original price
     * @return the discount amount, or 0 if voucher is not valid
     */
    public double calculateDiscount(double originalPrice) {
        if (!isValid()) {
            return 0;
        }
        return discountStrategy.calculateDiscount(originalPrice, value);
    }

    /**
     * Calculates the final price after applying the discount.
     * @param originalPrice the original price
     * @return the final price after discount, or original price if voucher is not valid
     */
    public double applyDiscount(double originalPrice) {
        if (originalPrice < 0) {
            return 0;
        }
        double discount = calculateDiscount(originalPrice);
        return Math.max(0, originalPrice - discount);
    }

    @Override
    public String toString() {
        return "Voucher{" +
                "code='" + code + '\'' +
                ", discountType=" + discountStrategy.getType() +
                ", value=" + value +
                ", expiry=" + expiry +
                ", active=" + active +
                ", valid=" + isValid() +
                '}';
    }
}

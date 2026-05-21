package model.order;

import model.beverage.Beverage;
import model.customer.Customer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a single customer order.
 *
 * OOP: Encapsulation — price and item logic are self-contained.
 * Implements Comparable<Order> so java.util.PriorityQueue can order VIP orders
 * by loyalty points (descending); ties broken by orderTime (ascending = FIFO).
 *
 * T09 — required by VIPOrderQueue.
 */
public class Order implements Comparable<Order> {

    // ── Auto-incrementing ID ────────────────────────────────────────────────
    private static int counter = 1;
    public  static void resetCounter() { counter = 1; } // for unit tests

    // ── Fields ──────────────────────────────────────────────────────────────
    private final int           orderId;
    private final Customer      customer;
    private final List<Beverage> items;
    private       OrderStatus   status;
    private final LocalDateTime orderTime;
    private       String        appliedVoucherCode;  // nullable
    private       double        voucherDiscount;

    // VIP threshold: customers with ≥100 loyalty points go to vipQueue
    public static final int VIP_THRESHOLD = 100;

    // Loyalty points earned: 1 pt per 10,000 VND spent
    private static final int POINTS_PER_VND = 10_000;

    // ── Constructor ─────────────────────────────────────────────────────────
    public Order(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("Customer cannot be null");
        this.orderId   = counter++;
        this.customer  = customer;
        this.items     = new ArrayList<>();
        this.status    = OrderStatus.PENDING;
        this.orderTime = LocalDateTime.now();
        this.voucherDiscount = 0;
    }

    // ── Item management ─────────────────────────────────────────────────────
    public void addItem(Beverage beverage) {
        if (beverage == null) throw new IllegalArgumentException("Beverage cannot be null");
        items.add(beverage);
    }

    public boolean removeItem(Beverage beverage) {
        return items.remove(beverage);
    }

    public List<Beverage> getItems() {
        return Collections.unmodifiableList(items);
    }

    // ── Price calculation ────────────────────────────────────────────────────
    /**
     * Sum of all item prices BEFORE any voucher discount.
     */
    public double getSubtotal() {
        return items.stream().mapToDouble(Beverage::calculatePrice).sum();
    }

    /**
     * Final price after applying voucher discount (if any).
     * Voucher discount is resolved by VoucherController before this is called.
     */
    public double getTotalPrice(double discountPercent) {
        double discount = Math.max(0, Math.min(100, discountPercent));
        return getSubtotal() * (1 - discount / 100.0);
    }

    public double getTotalPrice() {
        return Math.max(0, getSubtotal() - voucherDiscount);
    }

    public void applyVoucherDiscount(String code, double discountAmount) {
        if (code == null || code.isBlank() || discountAmount <= 0) {
            clearVoucher();
            return;
        }
        this.appliedVoucherCode = code.trim().toUpperCase();
        this.voucherDiscount = Math.min(discountAmount, getSubtotal());
    }

    public void clearVoucher() {
        this.appliedVoucherCode = null;
        this.voucherDiscount = 0;
    }

    public double getVoucherDiscount() {
        return voucherDiscount;
    }

    public Map<String, Integer> getIngredientRequirements() {
        Map<String, Integer> requirements = new LinkedHashMap<>();
        for (Beverage beverage : items) {
            beverage.getIngredientRequirements()
                .forEach((name, amount) -> requirements.merge(name, amount, Integer::sum));
        }
        return requirements;
    }

    // ── Loyalty points ───────────────────────────────────────────────────────
    /** Points to award when order is COMPLETED. */
    public int calculateEarnedPoints() {
        return (int) (getTotalPrice() / POINTS_PER_VND);
    }

    // ── VIP flag ─────────────────────────────────────────────────────────────
    public boolean isVip() {
        return customer.getLoyaltyPoints() >= VIP_THRESHOLD;
    }

    // ── Validation ───────────────────────────────────────────────────────────
    public boolean validate() {
        return !items.isEmpty();
    }

    // ── Comparable — used by PriorityQueue in VIPOrderQueue ─────────────────
    /**
     * Higher loyalty points → dequeued first.
     * Same points → earlier orderTime wins (FIFO within tier).
     */
    @Override
    public int compareTo(Order other) {
        int cmp = Integer.compare(
            other.customer.getLoyaltyPoints(),
            this.customer.getLoyaltyPoints()
        );
        if (cmp != 0) return cmp;
        return this.orderTime.compareTo(other.orderTime); // FIFO tiebreak
    }

    // ── Getters / Setters ────────────────────────────────────────────────────
    public int           getOrderId()            { return orderId; }
    public Customer      getCustomer()           { return customer; }
    public OrderStatus   getStatus()             { return status; }
    public LocalDateTime getOrderTime()          { return orderTime; }
    public String        getAppliedVoucherCode() { return appliedVoucherCode; }

    public void setStatus(OrderStatus status) {
        if (status == null) throw new IllegalArgumentException("Status cannot be null");
        this.status = status;
    }

    public void setAppliedVoucherCode(String code) { this.appliedVoucherCode = code; }

    // ── toString ─────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm dd/MM");
        return String.format(
            "Order #%d | %s | %s | %s | %.0f VND",
            orderId,
            customer.getName(),
            isVip() ? "VIP" : "Regular",
            fmt.format(orderTime),
            getTotalPrice()
        );
    }
}

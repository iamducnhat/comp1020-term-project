package controller;

import model.customer.Customer;
import util.SortUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all customer records.
 *
 * Data Structure: HashMap<String, Customer> keyed by phone number.
 * Provides O(1) lookup, add, and update.
 *
 * MVC: Controller layer — sits between CustomerPanel (view) and Customer (model).
 */
public class CustomerController {

    // Key: phone number (unique customer ID)
    private final Map<String, Customer> customers = new HashMap<>();

    // ── CRUD ─────────────────────────────────────────────────────────────────

    /**
     * Register a new customer.
     * @throws IllegalArgumentException if phone already exists.
     */
    public Customer registerCustomer(String name, String phone) {
        String key = normalizePhone(phone);
        if (customers.containsKey(key))
            throw new IllegalArgumentException("Customer with phone " + phone + " already exists.");

        Customer c = new Customer(name, key);
        customers.put(key, c);
        return c;
    }

    /**
     * O(1) lookup by phone.
     * @return Customer or null if not found.
     */
    public Customer findByPhone(String phone) {
        if (phone == null || phone.isBlank()) return null;
        return customers.get(normalizePhone(phone));
    }

    /**
     * Find or auto-register: used at POS when phone is entered.
     * If customer doesn't exist, registers them with a default name.
     */
    public Customer findOrRegister(String name, String phone) {
        Customer existing = findByPhone(phone);
        if (existing != null) return existing;
        return registerCustomer(name, phone);
    }

    public boolean removeCustomer(String phone) {
        if (phone == null || phone.isBlank()) return false;
        return customers.remove(normalizePhone(phone)) != null;
    }

    public boolean updateName(String phone, String newName) {
        if (phone == null || phone.isBlank()) return false;
        Customer c = findByPhone(phone);
        if (c == null) return false;
        c.setName(newName);
        return true;
    }

    public boolean updateEmail(String phone, String email) {
        if (phone == null || phone.isBlank()) return false;
        Customer c = findByPhone(phone);
        if (c == null) return false;
        c.setEmail(email);
        return true;
    }

    // ── Points ────────────────────────────────────────────────────────────────
    public void awardPoints(String phone, int points) {
        Customer c = findByPhone(phone);
        if (c != null) c.addPoints(points);
    }

    public boolean redeemPoints(String phone, int points) {
        Customer c = findByPhone(phone);
        return c != null && c.redeemPoints(points);
    }

    // ── Queries ───────────────────────────────────────────────────────────────
    public Collection<Customer> getAllCustomers() {
        return Collections.unmodifiableCollection(customers.values());
    }

    public List<Customer> getLeaderboard() {
        return SortUtil.sortByPointsDesc(customers.values().stream().toList());
    }

    public List<Customer> getAllSortedByName() {
        return SortUtil.sortByName(customers.values().stream().toList());
    }

    public boolean exists(String phone) {
        if (phone == null || phone.isBlank()) return false;
        return customers.containsKey(normalizePhone(phone));
    }

    public int getTotalCustomers() { return customers.size(); }

    // ── Seed data ─────────────────────────────────────────────────────────────
    public void seedSampleCustomers() {
        Customer alice = registerCustomer("Alice Nguyen",  "0901234567");
        alice.addPoints(250);  // VIP

        Customer bob = registerCustomer("Bob Tran",    "0912345678");
        bob.addPoints(80);

        registerCustomer("Carol Le",     "0923456789");
        registerCustomer("David Pham",   "0934567890");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank())
            throw new IllegalArgumentException("Phone number cannot be blank");
        return phone.trim();
    }
}

package controller;

import model.voucher.Voucher;
import model.voucher.Voucher.VoucherType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all discount vouchers.
 *
 * Data Structure: HashMap<String, Voucher> — O(1) lookup by voucher code.
 * MVC: Controller between VoucherPanel (view) and Voucher (model).
 */
public class VoucherController {

    // Key: voucher code (uppercase, trimmed)
    private final Map<String, Voucher> vouchers = new HashMap<>();

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public void addVoucher(Voucher voucher) {
        if (vouchers.containsKey(voucher.getCode()))
            throw new IllegalArgumentException("Voucher " + voucher.getCode() + " already exists.");
        vouchers.put(voucher.getCode(), voucher);
    }

    public void createVoucher(String code, VoucherType type,
                              double value, double minOrder) {
        Voucher v = new Voucher(code, type, value, minOrder);
        addVoucher(v);
    }

    public boolean removeVoucher(String code) {
        if (code == null || code.isBlank()) return false;
        return vouchers.remove(normalize(code)) != null;
    }

    // ── Lookup & Validation ───────────────────────────────────────────────────

    /**
     * O(1) lookup by code.
     * @return Voucher or null if not found.
     */
    public Voucher findVoucher(String code) {
        if (code == null || code.isBlank()) return null;
        return vouchers.get(normalize(code));
    }

    /**
     * Validate and calculate discount for a given order total.
     *
     * @return discount amount in VND (0 if code invalid/used/order below minimum).
     */
    public double applyVoucher(String code, double orderTotal) {
        Voucher v = findVoucher(code);
        if (v == null) return 0;
        return v.calculateDiscount(orderTotal);
    }

    /**
     * Mark voucher as used after a successful order.
     * @return true if marked successfully, false if code not found.
     */
    public boolean markUsed(String code) {
        Voucher v = findVoucher(code);
        if (v == null) return false;
        v.markUsed();
        return true;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public Collection<Voucher> getAllVouchers() {
        return Collections.unmodifiableCollection(vouchers.values());
    }

    public Collection<Voucher> getAvailableVouchers() {
        return vouchers.values().stream()
            .filter(v -> !v.isUsed())
            .toList();
    }

    public boolean isValid(String code, double orderTotal) {
        Voucher v = findVoucher(code);
        return v != null && v.isValid(orderTotal);
    }

    // ── Seed ─────────────────────────────────────────────────────────────────

    public void seedSampleVouchers() {
        createVoucher("WELCOME10", VoucherType.PERCENT, 10, 0);
        createVoucher("SAVE20K",   VoucherType.FIXED,   20_000, 100_000);
        createVoucher("VIP15",     VoucherType.PERCENT, 15, 50_000);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String normalize(String code) {
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("Voucher code cannot be blank");
        return code.trim().toUpperCase();
    }
}

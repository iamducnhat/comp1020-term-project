package controller;

import java.util.Collection;
import java.util.HashMap;
import model.voucher.Voucher;

/**
 * Controller for managing voucher operations in the coffee POS system.
 * Manages vouchers using a HashMap with voucher code as the key.
 */
public class VoucherController {
    private HashMap<String, Voucher> vouchers;

    /**
     * Constructs a VoucherController and initializes the vouchers HashMap.
     */
    public VoucherController() {
        this.vouchers = new HashMap<>();
    }

    /**
     * Adds a new voucher to the system.
     * @param voucher the voucher to add
     * @return true if voucher was added successfully, false if voucher with same code already exists
     */
    public boolean addVoucher(Voucher voucher) {
        if (voucher == null || voucher.getCode() == null || voucher.getCode().trim().isEmpty()) {
            return false;
        }

        if (vouchers.containsKey(voucher.getCode())) {
            return false; // Voucher with this code already exists
        }

        vouchers.put(voucher.getCode(), voucher);
        return true;
    }

    /**
     * Finds a voucher by its code.
     * @param code the voucher code to search for
     * @return the Voucher if found, null otherwise
     */
    public Voucher findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return vouchers.get(code);
    }

    /**
     * Applies a voucher to calculate the discounted price.
     * @param code the voucher code
     * @param originalPrice the original price before discount
     * @return the final price after discount, or original price if voucher is invalid
     */
    public double applyVoucher(String code, double originalPrice) {
        if (originalPrice < 0) {
            return 0;
        }

        Voucher voucher = findByCode(code);
        if (voucher == null) {
            return originalPrice;
        }

        return voucher.applyDiscount(originalPrice);
    }

    /**
     * Gets the discount amount for a voucher if valid.
     * @param code the voucher code
     * @param originalPrice the original price
     * @return the discount amount, or 0 if voucher is not valid
     */
    public double getDiscountAmount(String code, double originalPrice) {
        if (originalPrice < 0) {
            return 0;
        }

        Voucher voucher = findByCode(code);
        if (voucher == null) {
            return 0;
        }

        return voucher.calculateDiscount(originalPrice);
    }

    /**
     * Checks if a voucher code is valid.
     * @param code the voucher code
     * @return true if voucher exists and is valid, false otherwise
     */
    public boolean isValidVoucher(String code) {
        Voucher voucher = findByCode(code);
        return voucher != null && voucher.isValid();
    }

    /**
     * Deletes a voucher from the system.
     * @param code the voucher code
     * @return true if voucher was deleted successfully, false if voucher not found
     */
    public boolean deleteVoucher(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        if (vouchers.containsKey(code)) {
            vouchers.remove(code);
            return true;
        }

        return false;
    }

    /**
     * Deactivates a voucher.
     * @param code the voucher code
     * @return true if voucher was deactivated, false if voucher not found
     */
    public boolean deactivateVoucher(String code) {
        Voucher voucher = findByCode(code);
        if (voucher == null) {
            return false;
        }
        voucher.setActive(false);
        return true;
    }

    /**
     * Gets all vouchers in the system.
     * @return a collection of all vouchers
     */
    public Collection<Voucher> getAllVouchers() {
        return vouchers.values();
    }

    /**
     * Gets the total number of vouchers.
     * @return the number of vouchers in the system
     */
    public int getVoucherCount() {
        return vouchers.size();
    }
}

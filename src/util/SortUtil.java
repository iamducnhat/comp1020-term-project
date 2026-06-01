package util;

import model.customer.Customer;
import model.order.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorting and Binary Search utilities.
 *
 * All sort methods are O(n log n) — backed by a custom, hand-coded Generic Merge Sort.
 * Binary search requires a pre-sorted list — O(log n).
 *
 * T11 – Phase 5 Refactor | VinTony POS Loyalty System
 */
public class SortUtil {

    private SortUtil() {} // utility class — no instantiation

    // ══════════════════════════════════════════════════════════════════════════
    // CUSTOM GENERIC MERGE SORT ALGORITHM
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Custom generic Merge Sort implementation — O(n log n) stable sorting.
     */
    public static <T> void mergeSort(List<T> list, Comparator<? super T> c) {
        if (list == null || list.size() < 2) return;
        List<T> temp = new ArrayList<>(list);
        mergeSort(list, temp, 0, list.size() - 1, c);
    }

    private static <T> void mergeSort(List<T> list, List<T> temp, int left, int right, Comparator<? super T> c) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(list, temp, left, mid, c);
            mergeSort(list, temp, mid + 1, right, c);
            merge(list, temp, left, mid, right, c);
        }
    }

    private static <T> void merge(List<T> list, List<T> temp, int left, int mid, int right, Comparator<? super T> c) {
        for (int i = left; i <= right; i++) {
            temp.set(i, list.get(i));
        }

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (c.compare(temp.get(i), temp.get(j)) <= 0) {
                list.set(k++, temp.get(i++));
            } else {
                list.set(k++, temp.get(j++));
            }
        }

        while (i <= mid) {
            list.set(k++, temp.get(i++));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ORDER SORTING
    // ══════════════════════════════════════════════════════════════════════════

    /** Sort orders by orderTime ascending (oldest first). */
    public static List<Order> sortByTime(List<Order> orders) {
        List<Order> copy = new ArrayList<>(orders);
        mergeSort(copy, Comparator.comparing(Order::getOrderTime));
        return copy;
    }

    /** Sort orders by total price ascending. */
    public static List<Order> sortByPriceAsc(List<Order> orders) {
        List<Order> copy = new ArrayList<>(orders);
        mergeSort(copy, Comparator.comparingDouble(Order::getTotalPrice));
        return copy;
    }

    /** Sort orders by total price descending. */
    public static List<Order> sortByPriceDesc(List<Order> orders) {
        List<Order> copy = new ArrayList<>(orders);
        Comparator<Order> cmp = Comparator.comparingDouble(Order::getTotalPrice);
        mergeSort(copy, cmp.reversed());
        return copy;
    }

    /** Sort orders by customer loyalty points descending (VIP first). */
    public static List<Order> sortByLoyaltyDesc(List<Order> orders) {
        List<Order> copy = new ArrayList<>(orders);
        mergeSort(copy, Comparator
            .comparingInt((Order o) -> o.getCustomer().getLoyaltyPoints())
            .reversed());
        return copy;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CUSTOMER SORTING
    // ══════════════════════════════════════════════════════════════════════════

    /** Sort customers by loyalty points descending (leaderboard). */
    public static List<Customer> sortByPointsDesc(List<Customer> customers) {
        List<Customer> copy = new ArrayList<>(customers);
        mergeSort(copy, Comparator.comparingInt(Customer::getLoyaltyPoints).reversed());
        return copy;
    }

    /** Sort customers alphabetically by name. */
    public static List<Customer> sortByName(List<Customer> customers) {
        List<Customer> copy = new ArrayList<>(customers);
        mergeSort(copy, Comparator.comparing(Customer::getName, String.CASE_INSENSITIVE_ORDER));
        return copy;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BINARY SEARCH  (list must be sorted first!)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Binary search for an order by orderId in a list sorted by orderId ascending.
     *
     * @param sortedOrders list sorted by orderId ascending
     * @param targetId     orderId to find
     * @return the matching Order, or null if not found.
     */
    public static Order binarySearchById(List<Order> sortedOrders, int targetId) {
        int lo = 0, hi = sortedOrders.size() - 1;

        while (lo <= hi) {
            int mid    = lo + (hi - lo) / 2;
            int midId  = sortedOrders.get(mid).getOrderId();

            if      (midId == targetId) return sortedOrders.get(mid);
            else if (midId  < targetId) lo = mid + 1;
            else                        hi = mid - 1;
        }
        return null; // not found
    }

    /**
     * Binary search for a customer by name (case-insensitive) in a name-sorted list.
     *
     * @param sortedCustomers list sorted by name alphabetically
     * @param name            target name
     * @return first matching Customer, or null if not found.
     */
    public static Customer binarySearchByName(List<Customer> sortedCustomers, String name) {
        if (name == null || name.isBlank()) return null;
        String target = name.trim().toLowerCase();

        int lo = 0, hi = sortedCustomers.size() - 1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cmp = sortedCustomers.get(mid).getName().toLowerCase().compareTo(target);

            if      (cmp == 0) return sortedCustomers.get(mid);
            else if (cmp  < 0) lo = mid + 1;
            else               hi = mid - 1;
        }
        return null;
    }

    /**
     * Linear search by phone number — O(n) fallback when list is unsorted.
     */
    public static Customer linearSearchByPhone(List<Customer> customers, String phone) {
        if (phone == null) return null;
        String target = phone.trim();
        for (Customer c : customers) {
            if (c.getPhone().equals(target)) return c;
        }
        return null;
    }
}

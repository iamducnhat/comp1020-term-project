package controller;

import java.util.Collection;
import java.util.HashMap;
import model.customer.Customer;

/**
 * Controller for managing customer operations.
 * Manages customer data using a HashMap with phone number as the key.
 */
public class CustomerController {
    private HashMap<String, Customer> customers;

    /**
     * Constructs a CustomerController and initializes the customers HashMap.
     */
    public CustomerController() {
        this.customers = new HashMap<>();
    }

    /**
     * Adds a new customer to the system.
     * @param customer the customer to add
     * @return true if customer was added successfully, false if customer with same phone number already exists
     */
    public boolean addCustomer(Customer customer) {
        if (customer == null || customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty()) {
            return false;
        }

        if (customers.containsKey(customer.getPhoneNumber())) {
            return false; // Customer already exists
        }

        customers.put(customer.getPhoneNumber(), customer);
        return true;
    }

    /**
     * Finds a customer by phone number.
     * @param phoneNumber the phone number to search for
     * @return the Customer if found, null otherwise
     */
    public Customer findByPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }
        return customers.get(phoneNumber);
    }

    /**
     * Updates the loyalty points for a customer by adding points.
     * @param phoneNumber the phone number of the customer
     * @param points the number of points to add
     * @return true if update was successful, false if customer not found
     */
    public boolean addPoints(String phoneNumber, int points) {
        Customer customer = findByPhone(phoneNumber);
        if (customer == null) {
            return false;
        }
        customer.addPoints(points);
        return true;
    }

    /**
     * Redeems loyalty points for a customer.
     * @param phoneNumber the phone number of the customer
     * @param points the number of points to redeem
     * @return true if redemption was successful, false if customer not found or insufficient points
     */
    public boolean redeemPoints(String phoneNumber, int points) {
        Customer customer = findByPhone(phoneNumber);
        if (customer == null) {
            return false;
        }
        return customer.redeemPoints(points);
    }

    /**
     * Deletes a customer from the system.
     * @param phoneNumber the phone number of the customer to delete
     * @return true if customer was deleted successfully, false if customer not found
     */
    public boolean deleteCustomer(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        if (customers.containsKey(phoneNumber)) {
            customers.remove(phoneNumber);
            return true;
        }

        return false;
    }

    /**
     * Gets all customers in the system.
     * @return a collection of all customers
     */
    public Collection<Customer> getAllCustomers() {
        return customers.values();
    }

    /**
     * Gets the total number of customers.
     * @return the number of customers in the system
     */
    public int getCustomerCount() {
        return customers.size();
    }
}

package controller;

import model.order.Order;

import java.util.NoSuchElementException;

/**
 * OrderQueue — generic interface for all queue types.
 * Supports both FIFO (regular) and priority-based (VIP).
 *
 * T09 – Phase 3 Design | VinTony POS Loyalty System
 *
 * Implementations:
 *   RegularOrderQueue — LinkedList-backed FIFO
 *   VIPOrderQueue     — PriorityQueue-backed max-heap by loyaltyPoints
 */
public interface OrderQueue {

    /**
     * Add an order to the queue.
     * Regular: adds to tail (FIFO).
     * VIP: inserts by loyaltyPoints (max-heap).
     */
    void enqueue(Order order);

    /**
     * Remove and return the highest-priority order.
     * Regular: removes from head (FIFO).
     * VIP: removes order with most loyaltyPoints.
     *
     * @throws NoSuchElementException if the queue is empty.
     */
    Order dequeue();

    /**
     * Return the next order WITHOUT removing it.
     * Useful for display / preview logic.
     *
     * @throws NoSuchElementException if the queue is empty.
     */
    Order peek();

    /** Number of orders currently waiting. */
    int size();

    /** True if no orders are waiting. */
    boolean isEmpty();
}

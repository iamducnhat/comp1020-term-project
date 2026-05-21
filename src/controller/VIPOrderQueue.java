package controller;

import model.order.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

/**
 * VIP queue — backed by java.util.PriorityQueue.
 * Orders with MORE loyaltyPoints are dequeued first (max-heap).
 * Tie-break: earlier orderTime wins (FIFO within the same loyalty tier).
 *
 * Data Structure: PriorityQueue (binary heap)
 * enqueue (offer): O(log n)
 * dequeue (poll):  O(log n)
 * peek:            O(1)
 *
 * T09 – Phase 3 Design | VinTony POS Loyalty System
 */
public class VIPOrderQueue implements OrderQueue {

    // max-heap comparator: higher loyalty points → polled first
    private static final Comparator<Order> VIP_COMPARATOR =
        Comparator
            .comparingInt((Order o) -> o.getCustomer().getLoyaltyPoints())
            .reversed()                                  // more points = higher priority
            .thenComparing(Order::getOrderTime);         // FIFO tiebreak

    private final PriorityQueue<Order> queue =
        new PriorityQueue<>(VIP_COMPARATOR);

    @Override
    public void enqueue(Order order) {
        if (order == null) throw new IllegalArgumentException("Cannot enqueue null order");
        queue.offer(order);     // O(log n)
    }

    @Override
    public Order dequeue() {
        if (isEmpty())
            throw new NoSuchElementException("VIP queue is empty");
        return queue.poll();    // O(log n)
    }

    @Override
    public Order peek() {
        if (isEmpty())
            throw new NoSuchElementException("VIP queue is empty");
        return queue.peek();    // O(1)
    }

    @Override
    public int size()        { return queue.size(); }

    @Override
    public boolean isEmpty() { return queue.isEmpty(); }

    /**
     * Returns a sorted snapshot of all VIP orders for display purposes.
     * Does NOT remove any orders.
     */
    public List<Order> snapshot() {
        List<Order> sorted = new ArrayList<>(queue);
        sorted.sort(VIP_COMPARATOR);
        return sorted;
    }
}

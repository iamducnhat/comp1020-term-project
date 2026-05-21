package controller;

import model.order.Order;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Regular FIFO queue — backed by LinkedList.
 * First order in = first order out.
 *
 * Data Structure: LinkedList used as a Queue.
 * addLast()     → enqueue  O(1)
 * removeFirst() → dequeue  O(1)
 * peekFirst()   → peek     O(1)
 */
public class RegularOrderQueue implements OrderQueue {

    private final LinkedList<Order> queue = new LinkedList<>();

    @Override
    public void enqueue(Order order) {
        if (order == null) throw new IllegalArgumentException("Cannot enqueue null order");
        queue.addLast(order);           // O(1)
    }

    @Override
    public Order dequeue() {
        if (isEmpty())
            throw new NoSuchElementException("Regular queue is empty");
        return queue.removeFirst();     // O(1)
    }

    @Override
    public Order peek() {
        if (isEmpty())
            throw new NoSuchElementException("Regular queue is empty");
        return queue.peekFirst();
    }

    @Override
    public int size()      { return queue.size(); }

    @Override
    public boolean isEmpty() { return queue.isEmpty(); }

    /** Returns a snapshot view of all waiting orders (for display). */
    public java.util.List<Order> snapshot() {
        return java.util.Collections.unmodifiableList(queue);
    }
}

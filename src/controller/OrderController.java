package controller;

import model.beverage.Beverage;
import model.customer.Customer;
import model.order.Order;
import model.order.OrderStatus;
import util.ActionHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * OrderController — central hub for all order operations.
 *
 * Responsibilities:
 *   1. Route incoming orders to VIP or Regular queue.
 *   2. Process orders (dequeue → FIFO or priority).
 *   3. Maintain a completed-orders history list.
 *   4. Provide undo/redo for add/remove item actions via ActionHistory.
 *
 * Data Structures used:
 *   VIPOrderQueue     — PriorityQueue (max-heap by loyaltyPoints)
 *   RegularOrderQueue — LinkedList (FIFO)
 *   ActionHistory     — two ArrayDeque stacks (undo / redo)
 *
 * T10 – Phase 4 Core Dev | VinTony POS Loyalty System
 */
public class OrderController {

    // ── Queues ────────────────────────────────────────────────────────────────
    private final VIPOrderQueue     vipQueue     = new VIPOrderQueue();
    private final RegularOrderQueue regularQueue = new RegularOrderQueue();

    // ── Completed orders history ──────────────────────────────────────────────
    private final List<Order> completedOrders = new ArrayList<>();

    // ── Undo/Redo manager (tracks add/remove item actions per current order) ──
    private final ActionHistory<Order> actionHistory = new ActionHistory<>(30);

    // ── Active order being built (before placing) ─────────────────────────────
    private Order activeOrder;

    // ══════════════════════════════════════════════════════════════════════════
    // ORDER BUILDING  (before placing in queue)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Start building a new order for the given customer.
     * Any previous unsaved order is discarded.
     */
    public void startNewOrder(Customer customer) {
        activeOrder = new Order(customer);
        actionHistory.clear();
    }

    /** Add a beverage to the active order — supports undo. */
    public void addItemToActive(Beverage beverage) {
        if (activeOrder == null) throw new IllegalStateException("No active order. Call startNewOrder() first.");
        actionHistory.execute(
            "Add " + beverage.getDescription(),
            activeOrder,
            o -> o.addItem(beverage),
            o -> o.removeItem(beverage)
        );
    }

    /** Remove a beverage from the active order — supports undo. */
    public void removeItemFromActive(Beverage beverage) {
        if (activeOrder == null) throw new IllegalStateException("No active order.");
        actionHistory.execute(
            "Remove " + beverage.getDescription(),
            activeOrder,
            o -> o.removeItem(beverage),
            o -> o.addItem(beverage)
        );
    }

    /** Undo last item add/remove on the active order. */
    public String undoLastAction() { return actionHistory.undo(); }

    /** Redo last undone action on the active order. */
    public String redoLastAction() { return actionHistory.redo(); }

    public boolean canUndo() { return actionHistory.canUndo(); }
    public boolean canRedo() { return actionHistory.canRedo(); }

    public Order getActiveOrder() { return activeOrder; }

    // ══════════════════════════════════════════════════════════════════════════
    // QUEUE OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Validate and route the active order to the correct queue.
     * VIP customers → vipQueue (PriorityQueue)
     * Regular        → regularQueue (FIFO LinkedList)
     *
     * @throws IllegalStateException if active order is empty / invalid.
     */
    public void placeActiveOrder() {
        if (activeOrder == null) throw new IllegalStateException("No active order to place.");
        if (!activeOrder.validate()) throw new IllegalStateException("Order has no items.");

        activeOrder.setStatus(OrderStatus.PENDING);
        placeOrder(activeOrder);
        activeOrder = null;   // reset
    }

    /** Route any Order object to the correct queue (called by placeActiveOrder & tests). */
    public void placeOrder(Order order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");
        if (order.isVip())
            vipQueue.enqueue(order);
        else
            regularQueue.enqueue(order);
    }

    /**
     * Dequeue and process the next order.
     * VIP queue is always drained before the regular queue.
     *
     * Awards loyalty points to the customer after completion.
     *
     * @throws NoSuchElementException if both queues are empty.
     */
    public Order processNext() {
        return processNext(null);
    }

    public Order processNext(InventoryController inventoryCtrl) {
        Order next = peekNextOrder();
        if (inventoryCtrl != null) {
            inventoryCtrl.consumeIngredients(next.getIngredientRequirements());
        }

        Order order;
        if (!vipQueue.isEmpty())
            order = vipQueue.dequeue();
        else if (!regularQueue.isEmpty())
            order = regularQueue.dequeue();
        else
            throw new NoSuchElementException("No pending orders in either queue.");

        order.setStatus(OrderStatus.PROCESSING);
        // Simulate processing → mark complete and award points
        order.setStatus(OrderStatus.COMPLETED);
        order.getCustomer().addPoints(order.calculateEarnedPoints());

        completedOrders.add(order);
        return order;
    }

    private Order peekNextOrder() {
        if (!vipQueue.isEmpty())
            return vipQueue.peek();
        if (!regularQueue.isEmpty())
            return regularQueue.peek();
        throw new NoSuchElementException("No pending orders in either queue.");
    }

    /**
     * Cancel an order that is still PENDING in a queue.
     * Searches both queues by orderId, dequeues everything, removes target, re-queues rest.
     *
     * @return true if found and cancelled, false otherwise.
     */
    public boolean cancelOrder(int orderId) {
        // Try regular queue first
        if (cancelFromQueue(regularQueue, orderId)) return true;
        return cancelFromQueue(vipQueue, orderId);
    }

    private boolean cancelFromQueue(OrderQueue q, int orderId) {
        // Drain the queue, remove target, re-enqueue the rest
        List<Order> temp = new ArrayList<>();
        boolean found = false;

        while (!q.isEmpty()) {
            Order o = q.dequeue();
            if (o.getOrderId() == orderId) {
                o.setStatus(OrderStatus.CANCELLED);
                completedOrders.add(o);
                found = true;
            } else {
                temp.add(o);
            }
        }
        temp.forEach(q::enqueue);
        return found;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // QUEUE STATE QUERIES
    // ══════════════════════════════════════════════════════════════════════════

    public int getVipQueueSize()     { return vipQueue.size(); }
    public int getRegularQueueSize() { return regularQueue.size(); }
    public int getTotalQueueSize()   { return vipQueue.size() + regularQueue.size(); }
    public boolean hasOrders()       { return !vipQueue.isEmpty() || !regularQueue.isEmpty(); }

    /** Snapshot of VIP queue (sorted by priority, non-destructive). */
    public List<Order> getVipQueueSnapshot()     { return vipQueue.snapshot(); }

    /** Snapshot of regular queue (FIFO order, non-destructive). */
    public List<Order> getRegularQueueSnapshot() { return regularQueue.snapshot(); }

    /** All completed + cancelled orders. */
    public List<Order> getCompletedOrders() {
        return Collections.unmodifiableList(completedOrders);
    }
}

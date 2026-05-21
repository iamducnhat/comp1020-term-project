import controller.CustomerController;
import controller.InventoryController;
import controller.OrderController;
import controller.RegularOrderQueue;
import controller.VIPOrderQueue;
import controller.VoucherController;
import factory.BeverageFactory;
import model.addon.Topping;
import model.addon.Topping.ToppingType;
import model.beverage.Beverage;
import model.beverage.Coffee;
import model.beverage.Size;
import model.beverage.Tea;
import model.customer.Customer;
import model.inventory.Ingredient;
import model.inventory.InventoryManager;
import model.menu.MenuNode;
import model.menu.MenuTree;
import model.order.Order;
import model.voucher.Voucher;
import model.voucher.Voucher.VoucherType;
import util.SortUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VinTonyFeatureSmokeTest {
    private static int passed = 0;

    public static void main(String[] args) {
        testBeverageFactoryPolymorphismDecoratorAndRecipes();
        testSingletonInventoryCrudAndStockAlerts();
        testCustomerHashMapCrudAndLoyalty();
        testVoucherHashMapDiscountLifecycle();
        testOrderControllerUndoRedoVoucherAndInventoryPipeline();
        testRegularQueueFifoAndVipPriorityQueue();
        testMenuTreeTraversal();
        testSortingAndSearchUtilities();

        System.out.println();
        System.out.println("ALL FEATURE PIPELINES PASSED: " + passed + " checks");
    }

    private static void testBeverageFactoryPolymorphismDecoratorAndRecipes() {
        Beverage coffee = BeverageFactory.create("COFFEE", "LATTE", "M");
        Beverage tea = BeverageFactory.create("TEA", "GREEN_TEA", "L");
        assertTrue(coffee instanceof Coffee, "Factory creates Coffee");
        assertTrue(tea instanceof Tea, "Factory creates Tea");
        assertEquals(35_000, coffee.calculatePrice(), "Coffee polymorphic pricing");
        assertEquals(30_000, tea.calculatePrice(), "Tea polymorphic pricing");

        Beverage decorated = new Topping(new Topping(coffee, ToppingType.PEARL), ToppingType.CARAMEL_SYRUP);
        assertEquals(55_000, decorated.calculatePrice(), "Decorator adds stacked topping prices");
        assertTrue(decorated.getDescription().contains("Pearl") && decorated.getDescription().contains("Caramel"),
            "Decorator appends topping descriptions");
        assertEquals(36, decorated.getIngredientRequirements().get("Espresso Beans"), "Coffee recipe includes beans");
        assertEquals(320, decorated.getIngredientRequirements().get("Whole Milk"), "Latte recipe includes milk");
        assertEquals(40, decorated.getIngredientRequirements().get("Boba Pearls"), "Topping recipe includes boba");
        assertEquals(25, decorated.getIngredientRequirements().get("Caramel Syrup"), "Topping recipe includes syrup");
        pass("Factory, polymorphism, decorator, and ingredient recipes");
    }

    private static void testSingletonInventoryCrudAndStockAlerts() {
        InventoryManager first = InventoryManager.getInstance();
        InventoryManager second = InventoryManager.getInstance();
        assertTrue(first == second, "InventoryManager is singleton");

        InventoryController inventory = new InventoryController();
        inventory.addIngredient("Smoke Test Dust", 5, "g", 10);
        assertTrue(inventory.hasLowStock(), "Low-stock alert detects new low ingredient");
        assertTrue(inventory.getLowStockAlerts().stream().anyMatch(i -> i.getName().equals("Smoke Test Dust")),
            "Low-stock list contains low ingredient");
        assertTrue(inventory.restock("Smoke Test Dust", 20), "Inventory restock succeeds");
        assertTrue(inventory.consume("Smoke Test Dust", 7), "Inventory consume succeeds");
        Ingredient ingredient = inventory.getIngredient("Smoke Test Dust");
        assertEquals(18, ingredient.getQuantity(), "Inventory quantity updates after restock and consume");
        assertTrue(inventory.removeIngredient("Smoke Test Dust"), "Inventory remove succeeds");
        pass("Singleton inventory CRUD, restock, consume, and low-stock alert");
    }

    private static void testCustomerHashMapCrudAndLoyalty() {
        CustomerController customers = new CustomerController();
        Customer alice = customers.registerCustomer("Alice Smoke", "0911000001");
        Customer bob = customers.registerCustomer("Bob Smoke", "0911000002");
        alice.addPoints(120);
        bob.addPoints(40);

        assertTrue(customers.findByPhone("0911000001") == alice, "HashMap lookup by phone returns customer");
        assertTrue(customers.updateName("0911000002", "Bobby Smoke"), "Customer update name succeeds");
        assertTrue(customers.updateEmail("0911000002", "bob@example.test"), "Customer update email succeeds");
        assertEquals("Bobby Smoke", bob.getName(), "Customer name updated");
        assertTrue(customers.redeemPoints("0911000001", 20), "Customer redeem points succeeds");
        assertEquals(100, alice.getLoyaltyPoints(), "Customer points are encapsulated and updated");
        assertTrue(customers.removeCustomer("0911000002"), "Customer delete succeeds");
        assertTrue(customers.findByPhone("0911000002") == null, "Deleted customer no longer found");
        pass("Customer HashMap CRUD and loyalty points");
    }

    private static void testVoucherHashMapDiscountLifecycle() {
        VoucherController vouchers = new VoucherController();
        vouchers.createVoucher("SMOKE10", VoucherType.PERCENT, 10, 0);
        vouchers.createVoucher("SMOKE20K", VoucherType.FIXED, 20_000, 100_000);

        assertEquals(5_000, vouchers.applyVoucher("smoke10", 50_000), "Percentage voucher is case-insensitive");
        assertEquals(0, vouchers.applyVoucher("SMOKE20K", 90_000), "Voucher respects minimum order");
        assertEquals(20_000, vouchers.applyVoucher("SMOKE20K", 120_000), "Fixed voucher caps discount correctly");
        assertTrue(vouchers.markUsed("SMOKE10"), "Voucher can be marked used");
        assertEquals(0, vouchers.applyVoucher("SMOKE10", 50_000), "Used voucher no longer applies");
        assertTrue(vouchers.removeVoucher("SMOKE20K"), "Voucher delete succeeds");
        pass("Voucher HashMap lookup, discounts, minimum order, use, and remove");
    }

    private static void testOrderControllerUndoRedoVoucherAndInventoryPipeline() {
        InventoryController inventory = new InventoryController();
        VoucherController vouchers = new VoucherController();
        vouchers.createVoucher("PIPE10", VoucherType.PERCENT, 10, 0);

        Customer customer = new Customer("Pipeline Customer", "0922000001");
        customer.addPoints(150);
        OrderController orders = new OrderController();
        orders.startNewOrder(customer);

        Beverage latte = new Topping(BeverageFactory.create("COFFEE", "LATTE", "M"), ToppingType.PEARL);
        Beverage tea = BeverageFactory.create("TEA", "JASMINE", "S");
        orders.addItemToActive(latte);
        orders.addItemToActive(tea);
        assertEquals(68_000, orders.getActiveOrder().getSubtotal(), "Active order subtotal before undo");

        orders.undoLastAction();
        assertEquals(45_000, orders.getActiveOrder().getSubtotal(), "Undo removes last item");
        orders.redoLastAction();
        assertEquals(68_000, orders.getActiveOrder().getSubtotal(), "Redo restores last item");

        double discount = vouchers.applyVoucher("PIPE10", orders.getActiveOrder().getSubtotal());
        orders.getActiveOrder().applyVoucherDiscount("PIPE10", discount);
        assertEquals(61_200, orders.getActiveOrder().getTotalPrice(), "Voucher discount affects order total");

        int beansBefore = inventory.getIngredient("Espresso Beans").getQuantity();
        int bobaBefore = inventory.getIngredient("Boba Pearls").getQuantity();
        orders.placeActiveOrder();
        vouchers.markUsed("PIPE10");
        Order completed = orders.processNext(inventory);

        assertEquals("Completed", completed.getStatus().getLabel(), "Order processing completes order");
        assertEquals(0, orders.getTotalQueueSize(), "Queue is empty after processing");
        assertEquals(156, customer.getLoyaltyPoints(), "Completed order awards loyalty based on final total");
        assertTrue(inventory.getIngredient("Espresso Beans").getQuantity() < beansBefore,
            "Processing consumes coffee ingredients");
        assertTrue(inventory.getIngredient("Boba Pearls").getQuantity() < bobaBefore,
            "Processing consumes topping ingredients");
        pass("Order pipeline: active cart, undo/redo, voucher, queue, processing, inventory, loyalty");
    }

    private static void testRegularQueueFifoAndVipPriorityQueue() {
        Customer regularA = customerWithPoints("Regular A", "0933000001", 0);
        Customer regularB = customerWithPoints("Regular B", "0933000002", 0);
        RegularOrderQueue regularQueue = new RegularOrderQueue();
        Order orderA = orderWithDrink(regularA, "TEA", "BLACK_TEA", "S");
        Order orderB = orderWithDrink(regularB, "TEA", "OOLONG", "S");
        regularQueue.enqueue(orderA);
        regularQueue.enqueue(orderB);
        assertTrue(regularQueue.dequeue() == orderA && regularQueue.dequeue() == orderB,
            "Regular queue preserves FIFO order");

        Customer vipLow = customerWithPoints("VIP Low", "0933000010", 120);
        Customer vipHigh = customerWithPoints("VIP High", "0933000011", 300);
        VIPOrderQueue vipQueue = new VIPOrderQueue();
        Order lowOrder = orderWithDrink(vipLow, "COFFEE", "AMERICANO", "S");
        Order highOrder = orderWithDrink(vipHigh, "COFFEE", "ESPRESSO", "S");
        vipQueue.enqueue(lowOrder);
        vipQueue.enqueue(highOrder);
        assertTrue(vipQueue.dequeue() == highOrder, "VIP priority queue dequeues higher loyalty first");

        OrderController controller = new OrderController();
        controller.placeOrder(orderWithDrink(regularA, "TEA", "JASMINE", "S"));
        controller.placeOrder(orderWithDrink(vipLow, "COFFEE", "LATTE", "S"));
        assertTrue(controller.processNext().getCustomer() == vipLow, "OrderController processes VIP before regular");
        assertTrue(controller.cancelOrder(controller.getRegularQueueSnapshot().get(0).getOrderId()),
            "OrderController cancels queued regular order by ID");
        pass("FIFO queue, VIP PriorityQueue, VIP routing, and cancel order");
    }

    private static void testMenuTreeTraversal() {
        MenuTree menu = new MenuTree();
        List<MenuNode> leaves = menu.getAllLeaves();
        assertEquals(24, leaves.size(), "Menu tree contains all drink-size leaves");
        assertTrue(leaves.stream().allMatch(MenuNode::isLeaf), "Menu tree leaf traversal returns only orderable nodes");
        assertTrue(leaves.stream().anyMatch(n -> n.getLabel().contains("Latte") && n.getBeverage() != null),
            "Menu tree includes Latte beverage leaf");
        pass("Menu tree hierarchy and traversal");
    }

    private static void testSortingAndSearchUtilities() {
        Customer alpha = customerWithPoints("Alpha", "0944000001", 10);
        Customer charlie = customerWithPoints("Charlie", "0944000003", 200);
        Customer bravo = customerWithPoints("Bravo", "0944000002", 80);
        List<Customer> customers = List.of(alpha, charlie, bravo);

        List<Customer> byPoints = SortUtil.sortByPointsDesc(customers);
        assertTrue(byPoints.get(0) == charlie && byPoints.get(2) == alpha, "Sort customers by points descending");

        List<Customer> byName = SortUtil.sortByName(customers);
        assertTrue(SortUtil.binarySearchByName(byName, "Bravo") == bravo, "Binary search customer by sorted name");
        assertTrue(SortUtil.linearSearchByPhone(customers, "0944000003") == charlie, "Linear phone lookup fallback");

        Order cheap = orderWithDrink(alpha, "TEA", "GREEN_TEA", "S");
        Order expensive = orderWithDrink(charlie, "COFFEE", "CAPPUCCINO", "L");
        Order middle = orderWithDrink(bravo, "COFFEE", "ESPRESSO", "S");
        List<Order> orders = List.of(expensive, cheap, middle);
        assertTrue(SortUtil.sortByPriceAsc(orders).get(0) == cheap, "Sort orders by price ascending");
        assertTrue(SortUtil.sortByPriceDesc(orders).get(0) == expensive, "Sort orders by price descending");
        assertTrue(SortUtil.sortByLoyaltyDesc(orders).get(0).getCustomer() == charlie,
            "Sort orders by customer loyalty descending");

        List<Order> byId = new ArrayList<>(orders);
        byId.sort(Comparator.comparingInt(Order::getOrderId));
        assertTrue(SortUtil.binarySearchById(byId, middle.getOrderId()) == middle, "Binary search order by ID");
        pass("Sorting and search utilities");
    }

    private static Order orderWithDrink(Customer customer, String category, String type, String size) {
        Order order = new Order(customer);
        order.addItem(BeverageFactory.create(category, type, size));
        return order;
    }

    private static Customer customerWithPoints(String name, String phone, int points) {
        Customer customer = new Customer(name, phone);
        customer.addPoints(points);
        return customer;
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label);
        }
    }

    private static void assertEquals(double expected, double actual, String label) {
        if (Math.abs(expected - actual) > 0.0001) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but got " + actual);
        }
    }

    private static void pass(String label) {
        passed++;
        System.out.println("PASS " + passed + " - " + label);
    }
}

package view;

import controller.CustomerController;
import controller.InventoryController;
import controller.OrderController;
import controller.VoucherController;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window — uses JTabbedPane with MVC architecture.
 *
 * Tabs:
 *   1. Orders       — place & process orders
 *   2. Customers    — manage customer loyalty
 *   3. Inventory    — monitor stock
 *   4. Vouchers     — manage discount codes
 */
public class MainFrame extends JFrame {

    // ── Shared controllers (single instance per session) ─────────────────────
    private final OrderController     orderCtrl     = new OrderController();
    private final CustomerController  customerCtrl  = new CustomerController();
    private final InventoryController inventoryCtrl = new InventoryController();
    private final VoucherController   voucherCtrl   = new VoucherController();

    private CustomerPanel customerPanel;
    private InventoryPanel inventoryPanel;
    private VoucherPanel voucherPanel;

    public MainFrame() {
        super("VinTony Coffee POS & Loyalty System");

        // Seed sample data
        customerCtrl.seedSampleCustomers();
        voucherCtrl.seedSampleVouchers();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 680);
        setMinimumSize(new Dimension(800, 580));
        setLocationRelativeTo(null);

        // ── Tab pane ──────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        customerPanel = new CustomerPanel(customerCtrl);
        inventoryPanel = new InventoryPanel(inventoryCtrl);
        voucherPanel = new VoucherPanel(voucherCtrl);

        tabs.addTab("🧾 Orders", buildOrderPanel());
        tabs.addTab("👥 Customers", customerPanel);
        tabs.addTab("📦 Inventory", inventoryPanel);
        tabs.addTab("🏷 Vouchers", voucherPanel);

        // ── Status bar ───────────────────────────────────────────────────────
        JLabel statusBar = new JLabel(" VinTony POS v1.0  |  COMP1020 Spring 2026");
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        statusBar.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        add(tabs, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel buildOrderPanel() {
        return new OrderPanel(orderCtrl, customerCtrl, voucherCtrl, inventoryCtrl, this::refreshDataPanels);
    }

    private void refreshDataPanels() {
        if (customerPanel != null) customerPanel.refreshList();
        if (inventoryPanel != null) inventoryPanel.refreshList();
        if (voucherPanel != null) voucherPanel.refreshList();
    }

    // ── Helper: apply a simple modern look ───────────────────────────────────
    public static void applyLookAndFeel() {
        try {
            // Try system L&F; fall back to Nimbus
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { /* keep default */ }
    }
}

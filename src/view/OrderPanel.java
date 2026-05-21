package view;

import controller.CustomerController;
import controller.InventoryController;
import controller.OrderController;
import controller.VoucherController;
import factory.BeverageFactory;
import model.beverage.Beverage;
import model.beverage.Coffee.CoffeeType;
import model.beverage.Size;
import model.beverage.Tea.TeaType;
import model.addon.Topping;
import model.addon.Topping.ToppingType;
import model.customer.Customer;
import model.order.Order;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Order management panel.
 *
 * Features:
 *   - Look up / register customer by phone
 *   - Browse menu (Coffee / Tea + Size)
 *   - Add toppings via Decorator pattern
 *   - Apply voucher
 *   - Undo / Redo item changes
 *   - Place order → routed to VIP or Regular queue
 *   - Process next order button
 *   - View both queues in real time
 */
public class OrderPanel extends JPanel {

    private final OrderController    orderCtrl;
    private final CustomerController customerCtrl;
    private final VoucherController  voucherCtrl;
    private final InventoryController inventoryCtrl;
    private final Runnable           onDataChanged;

    // ── Customer section ─────────────────────────────────────────────────────
    private JTextField tfPhone, tfName;
    private JLabel     lblCustomerInfo;
    private Customer   currentCustomer;

    // ── Beverage selection ────────────────────────────────────────────────────
    private JComboBox<String> cbCategory, cbType, cbSize;
    private JComboBox<String> cbTopping;
    private JCheckBox          chkTopping;

    // ── Order items list ─────────────────────────────────────────────────────
    private DefaultListModel<String> orderItemsModel;
    private JList<String>            orderItemsList;
    private List<Beverage>           currentItems; // parallel list for removal
    private JLabel                   lblTotal;

    // ── Voucher ───────────────────────────────────────────────────────────────
    private JTextField tfVoucher;
    private JLabel     lblDiscount;

    // ── Queue display ─────────────────────────────────────────────────────────
    private DefaultListModel<String> vipModel, regularModel;
    private JLabel                   lblVipCount, lblRegularCount;
    private JTextField               tfCancelOrderId;

    // ── Undo/Redo ─────────────────────────────────────────────────────────────
    private JButton btnUndo, btnRedo;

    // ── Log area ──────────────────────────────────────────────────────────────
    private JTextArea logArea;

    public OrderPanel(OrderController orderCtrl,
                      CustomerController customerCtrl,
                      VoucherController voucherCtrl,
                      InventoryController inventoryCtrl,
                      Runnable onDataChanged) {
        this.orderCtrl    = orderCtrl;
        this.customerCtrl = customerCtrl;
        this.voucherCtrl  = voucherCtrl;
        this.inventoryCtrl = inventoryCtrl;
        this.onDataChanged = onDataChanged == null ? () -> { } : onDataChanged;
        this.currentItems = new ArrayList<>();

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(buildLeftPanel(),   BorderLayout.WEST);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildRightPanel(),  BorderLayout.EAST);
        add(buildLogPanel(),    BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PANEL BUILDERS
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(230, 0));

        // ── Customer lookup ───────────────────────────────────────────────────
        JPanel custPanel = titledPanel("Customer");
        custPanel.setLayout(new GridLayout(0, 1, 4, 4));

        tfPhone = new JTextField(); tfPhone.setToolTipText("Customer phone number");
        tfName  = new JTextField(); tfName.setToolTipText("Name (for new customers)");
        lblCustomerInfo = new JLabel(" ");
        lblCustomerInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        JButton btnLookup = new JButton("Lookup / Register");
        btnLookup.addActionListener(e -> lookupOrRegisterCustomer());

        custPanel.add(new JLabel("Phone:"));
        custPanel.add(tfPhone);
        custPanel.add(new JLabel("Name:"));
        custPanel.add(tfName);
        custPanel.add(btnLookup);
        custPanel.add(lblCustomerInfo);

        // ── Beverage selector ─────────────────────────────────────────────────
        JPanel menuPanel = titledPanel("Menu");
        menuPanel.setLayout(new GridLayout(0, 1, 4, 4));

        cbCategory = new JComboBox<>(new String[]{"COFFEE", "TEA"});
        cbType     = new JComboBox<>();
        cbSize     = new JComboBox<>(new String[]{"S", "M", "L"});
        chkTopping = new JCheckBox("Add Topping");
        cbTopping  = new JComboBox<>();
        cbTopping.setEnabled(false);

        // Populate type combos
        populateCoffeeTypes();
        cbCategory.addActionListener(e -> updateTypeCombo());

        chkTopping.addActionListener(e -> cbTopping.setEnabled(chkTopping.isSelected()));

        for (ToppingType t : ToppingType.values())
            cbTopping.addItem(t.getLabel());

        JButton btnAdd = new JButton("Add to Order");
        btnAdd.addActionListener(e -> addItemToOrder());

        menuPanel.add(new JLabel("Category:")); menuPanel.add(cbCategory);
        menuPanel.add(new JLabel("Type:"));     menuPanel.add(cbType);
        menuPanel.add(new JLabel("Size:"));     menuPanel.add(cbSize);
        menuPanel.add(chkTopping);
        menuPanel.add(cbTopping);
        menuPanel.add(btnAdd);

        // ── Voucher ───────────────────────────────────────────────────────────
        JPanel voucherPanel = titledPanel("Voucher");
        voucherPanel.setLayout(new GridLayout(0, 1, 4, 4));
        tfVoucher   = new JTextField();
        lblDiscount = new JLabel(" ");
        JButton btnApply = new JButton("Apply");
        btnApply.addActionListener(e -> applyVoucher());
        voucherPanel.add(new JLabel("Code:")); voucherPanel.add(tfVoucher);
        voucherPanel.add(btnApply);
        voucherPanel.add(lblDiscount);

        panel.add(custPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(menuPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(voucherPanel);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = titledPanel("Current Order");
        panel.setLayout(new BorderLayout(4, 4));

        orderItemsModel = new DefaultListModel<>();
        orderItemsList  = new JList<>(orderItemsModel);
        orderItemsList.setFont(new Font("Monospaced", Font.PLAIN, 12));

        lblTotal = new JLabel("Total: 0 VND");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Undo / Redo
        btnUndo = new JButton("Undo");
        btnRedo = new JButton("Redo");
        btnUndo.setEnabled(false);
        btnRedo.setEnabled(false);

        btnUndo.addActionListener(e -> {
            String msg = orderCtrl.undoLastAction();
            if (msg != null) { refreshOrderList(); log(msg); }
            updateUndoRedo();
        });
        btnRedo.addActionListener(e -> {
            String msg = orderCtrl.redoLastAction();
            if (msg != null) { refreshOrderList(); log(msg); }
            updateUndoRedo();
        });

        JButton btnRemove = new JButton("Remove Selected");
        btnRemove.addActionListener(e -> removeSelectedItem());

        JButton btnPlace = new JButton("Place Order");
        btnPlace.setBackground(new Color(70, 160, 90));
        btnPlace.setForeground(Color.WHITE);
        btnPlace.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPlace.addActionListener(e -> placeOrder());

        JButton btnProcess = new JButton("Process Next");
        btnProcess.setBackground(new Color(60, 120, 200));
        btnProcess.setForeground(Color.WHITE);
        btnProcess.addActionListener(e -> processNextOrder());

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        buttonRow.add(btnUndo); buttonRow.add(btnRedo);
        buttonRow.add(btnRemove); buttonRow.add(btnPlace); buttonRow.add(btnProcess);

        panel.add(new JScrollPane(orderItemsList), BorderLayout.CENTER);
        panel.add(lblTotal,   BorderLayout.NORTH);
        panel.add(buttonRow, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(220, 0));

        // ── VIP Queue ─────────────────────────────────────────────────────────
        JPanel vipPanel = titledPanel("VIP Queue");
        vipPanel.setLayout(new BorderLayout());
        vipModel     = new DefaultListModel<>();
        lblVipCount  = new JLabel("0 orders");
        JList<String> vipList = new JList<>(vipModel);
        vipList.setFont(new Font("Monospaced", Font.PLAIN, 11));
        vipPanel.add(lblVipCount, BorderLayout.NORTH);
        vipPanel.add(new JScrollPane(vipList), BorderLayout.CENTER);

        // ── Regular Queue ─────────────────────────────────────────────────────
        JPanel regPanel = titledPanel("Regular Queue");
        regPanel.setLayout(new BorderLayout());
        regularModel    = new DefaultListModel<>();
        lblRegularCount = new JLabel("0 orders");
        JList<String> regList = new JList<>(regularModel);
        regList.setFont(new Font("Monospaced", Font.PLAIN, 11));
        regPanel.add(lblRegularCount, BorderLayout.NORTH);
        regPanel.add(new JScrollPane(regList), BorderLayout.CENTER);

        JPanel cancelPanel = titledPanel("Cancel Order");
        cancelPanel.setLayout(new GridLayout(0, 1, 4, 4));
        tfCancelOrderId = new JTextField();
        JButton btnCancel = new JButton("Cancel by ID");
        btnCancel.addActionListener(e -> cancelOrder());
        cancelPanel.add(new JLabel("Order ID:"));
        cancelPanel.add(tfCancelOrderId);
        cancelPanel.add(btnCancel);

        panel.add(vipPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(regPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(cancelPanel);
        return panel;
    }

    private JPanel buildLogPanel() {
        JPanel panel = titledPanel("Activity Log");
        panel.setPreferredSize(new Dimension(0, 110));
        panel.setLayout(new BorderLayout());
        logArea = new JTextArea(4, 0);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACTION HANDLERS
    // ══════════════════════════════════════════════════════════════════════════

    private void lookupOrRegisterCustomer() {
        String phone = tfPhone.getText().trim();
        String name  = tfName.getText().trim();
        if (phone.isEmpty()) { showError("Enter phone number."); return; }
        if (name.isEmpty())  name = "Guest";

        currentCustomer = customerCtrl.findOrRegister(name, phone);
        orderCtrl.startNewOrder(currentCustomer);
        currentItems.clear();
        orderItemsModel.clear();
        lblTotal.setText("Total: 0 VND");

        lblCustomerInfo.setText(currentCustomer.isVip()
            ? "VIP | " + currentCustomer.getLoyaltyPoints() + " pts"
            : currentCustomer.getLoyaltyPoints() + " pts");
        updateUndoRedo();
        log("Customer: " + currentCustomer);
    }

    private void addItemToOrder() {
        if (currentCustomer == null) { showError("Look up a customer first."); return; }

        String category = (String) cbCategory.getSelectedItem();
        String type     = (String) cbType.getSelectedItem();
        String size     = (String) cbSize.getSelectedItem();
        if (type == null) return;

        try {
            Beverage bev = BeverageFactory.create(category, type, size);

            // Apply topping decorator if selected
            if (chkTopping.isSelected() && cbTopping.getSelectedIndex() >= 0) {
                ToppingType tt = ToppingType.values()[cbTopping.getSelectedIndex()];
                bev = new Topping(bev, tt);
            }

            orderCtrl.addItemToActive(bev);
            refreshOrderList();
            updateUndoRedo();
            log("Added: " + bev.getDescription());
        } catch (Exception ex) {
            showError("Error adding item: " + ex.getMessage());
        }
    }

    private void removeSelectedItem() {
        int idx = orderItemsList.getSelectedIndex();
        Order active = orderCtrl.getActiveOrder();
        if (active == null || idx < 0 || idx >= active.getItems().size()) return;
        Beverage bev = active.getItems().get(idx);
        orderCtrl.removeItemFromActive(bev);
        refreshOrderList();
        updateUndoRedo();
        log("Removed: " + bev.getDescription());
    }

    private void applyVoucher() {
        applyVoucherToActive(true);
    }

    private boolean applyVoucherToActive(boolean showMessages) {
        String code = tfVoucher.getText().trim();
        Order active = orderCtrl.getActiveOrder();
        if (currentCustomer == null || active == null) return false;
        if (code.isEmpty()) {
            active.clearVoucher();
            lblDiscount.setText(" ");
            refreshOrderList();
            return true;
        }

        double subtotal  = active.getSubtotal();
        double discount  = voucherCtrl.applyVoucher(code, subtotal);
        if (discount <= 0) {
            active.clearVoucher();
            lblDiscount.setText("Invalid / not applicable");
            lblDiscount.setForeground(Color.RED);
            refreshOrderList();
            if (showMessages) showError("Voucher is invalid, already used, or below minimum order.");
            return false;
        } else {
            active.applyVoucherDiscount(code, discount);
            lblDiscount.setText(String.format("-%.0f VND", discount));
            lblDiscount.setForeground(new Color(0, 130, 0));
            refreshOrderList();
            if (showMessages) log(String.format("Voucher %s: -%.0f VND", code, discount));
            return true;
        }
    }

    private void placeOrder() {
        if (currentCustomer == null) { showError("Look up a customer first."); return; }
        try {
            Order active = orderCtrl.getActiveOrder();
            if (active == null) { showError("No active order."); return; }
            if (!tfVoucher.getText().trim().isEmpty() && !applyVoucherToActive(false)) {
                showError("Voucher is invalid, already used, or below minimum order.");
                return;
            }
            String usedVoucher = active.getAppliedVoucherCode();
            orderCtrl.placeActiveOrder();
            if (usedVoucher != null) {
                voucherCtrl.markUsed(usedVoucher);
            }
            currentItems.clear();
            orderItemsModel.clear();
            lblTotal.setText("Total: 0 VND");
            tfVoucher.setText("");
            lblDiscount.setText(" ");
            currentCustomer = null;
            lblCustomerInfo.setText(" ");
            tfPhone.setText(""); tfName.setText("");
            updateUndoRedo();
            refreshQueues();
            onDataChanged.run();
            log("Order placed" + (usedVoucher == null ? "!" : " with voucher " + usedVoucher + "!"));
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void processNextOrder() {
        try {
            Order done = orderCtrl.processNext(inventoryCtrl);
            refreshQueues();
            onDataChanged.run();
            log("Processed: " + done);
            log("   +" + done.calculateEarnedPoints() + " pts -> " + done.getCustomer().getName());
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void cancelOrder() {
        try {
            int orderId = Integer.parseInt(tfCancelOrderId.getText().trim());
            boolean cancelled = orderCtrl.cancelOrder(orderId);
            refreshQueues();
            tfCancelOrderId.setText("");
            log(cancelled ? "Cancelled order #" + orderId : "Order #" + orderId + " not found in queue");
        } catch (NumberFormatException ex) {
            showError("Order ID must be a number.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // REFRESH HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private void refreshOrderList() {
        Order active = orderCtrl.getActiveOrder();
        currentItems.clear();
        orderItemsModel.clear();
        if (active == null) { lblTotal.setText("Total: 0 VND"); return; }

        refreshVoucherDiscount(active);

        currentItems = new ArrayList<>(active.getItems());
        for (Beverage b : currentItems)
            orderItemsModel.addElement(String.format("  %-30s %7.0f VND", b.getDescription(), b.calculatePrice()));

        if (active.getVoucherDiscount() > 0) {
            lblTotal.setText(String.format(
                "Subtotal: %.0f VND | Discount: %.0f VND | Total: %.0f VND",
                active.getSubtotal(), active.getVoucherDiscount(), active.getTotalPrice()));
        } else {
            lblTotal.setText(String.format("Total: %.0f VND", active.getTotalPrice()));
        }
    }

    private void refreshVoucherDiscount(Order active) {
        String code = active.getAppliedVoucherCode();
        if (code == null || code.isBlank()) return;
        double discount = voucherCtrl.applyVoucher(code, active.getSubtotal());
        if (discount <= 0) {
            active.clearVoucher();
            lblDiscount.setText("Voucher no longer applies");
            lblDiscount.setForeground(Color.RED);
            return;
        }
        active.applyVoucherDiscount(code, discount);
        lblDiscount.setText(String.format("-%.0f VND", discount));
        lblDiscount.setForeground(new Color(0, 130, 0));
    }

    private void refreshQueues() {
        vipModel.clear();
        for (Order o : orderCtrl.getVipQueueSnapshot())
            vipModel.addElement("#" + o.getOrderId() + " " + o.getCustomer().getName() +
                String.format(" (%dpts) %.0f VND", o.getCustomer().getLoyaltyPoints(), o.getTotalPrice()));
        lblVipCount.setText(orderCtrl.getVipQueueSize() + " orders");

        regularModel.clear();
        for (Order o : orderCtrl.getRegularQueueSnapshot())
            regularModel.addElement(String.format("#%d %s %.0f VND",
                o.getOrderId(), o.getCustomer().getName(), o.getTotalPrice()));
        lblRegularCount.setText(orderCtrl.getRegularQueueSize() + " orders");
    }

    private void updateUndoRedo() {
        btnUndo.setEnabled(orderCtrl.canUndo());
        btnRedo.setEnabled(orderCtrl.canRedo());
    }

    private void updateTypeCombo() {
        cbType.removeAllItems();
        if ("COFFEE".equals(cbCategory.getSelectedItem()))
            populateCoffeeTypes();
        else
            populateTeaTypes();
    }

    private void populateCoffeeTypes() {
        cbType.removeAllItems();
        for (CoffeeType ct : CoffeeType.values()) cbType.addItem(ct.name());
    }
    private void populateTeaTypes() {
        cbType.removeAllItems();
        for (TeaType tt : TeaType.values()) cbType.addItem(tt.name());
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    private JPanel titledPanel(String title) {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), title,
            TitledBorder.LEFT, TitledBorder.TOP));
        return p;
    }
}

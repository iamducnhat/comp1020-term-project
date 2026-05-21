package view;

import controller.InventoryController;
import model.inventory.Ingredient;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Inventory management panel.
 * Shows stock levels with low-stock warnings.
 */
public class InventoryPanel extends JPanel {

    private final InventoryController ctrl;

    private DefaultListModel<String> listModel;
    private JList<String>            ingredientList;
    private JTextField               tfName, tfQty, tfUnit, tfThreshold;
    private JTextArea                logArea;

    public InventoryPanel(InventoryController ctrl) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(buildFormPanel(), BorderLayout.WEST);
        add(buildListPanel(), BorderLayout.CENTER);
        add(buildLogPanel(),  BorderLayout.SOUTH);

        refreshList();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(240, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // ── Add ingredient ────────────────────────────────────────────────────
        JPanel addPanel = titledPanel("Add Ingredient");
        addPanel.setLayout(new GridLayout(0, 1, 4, 4));
        tfName = new JTextField(); tfQty = new JTextField("100");
        tfUnit = new JTextField("g"); tfThreshold = new JTextField("20");
        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> addIngredient());
        addPanel.add(new JLabel("Name:"));          addPanel.add(tfName);
        addPanel.add(new JLabel("Quantity:"));       addPanel.add(tfQty);
        addPanel.add(new JLabel("Unit:"));           addPanel.add(tfUnit);
        addPanel.add(new JLabel("Low threshold:")); addPanel.add(tfThreshold);
        addPanel.add(btnAdd);

        // ── Restock ───────────────────────────────────────────────────────────
        JPanel restockPanel = titledPanel("Restock");
        restockPanel.setLayout(new GridLayout(0, 1, 4, 4));
        JTextField tfRName = new JTextField();
        JTextField tfRAmt  = new JTextField("100");
        JButton btnRestock = new JButton("Restock");
        btnRestock.addActionListener(e -> {
            try {
                boolean ok = ctrl.restock(tfRName.getText().trim(),
                    Integer.parseInt(tfRAmt.getText().trim()));
                refreshList();
                log(ok ? "Restocked " + tfRName.getText() : "Ingredient not found");
            } catch (Exception ex) { showError(ex.getMessage()); }
        });
        restockPanel.add(new JLabel("Name:"));   restockPanel.add(tfRName);
        restockPanel.add(new JLabel("Amount:")); restockPanel.add(tfRAmt);
        restockPanel.add(btnRestock);

        JPanel managePanel = titledPanel("Use / Remove");
        managePanel.setLayout(new GridLayout(0, 1, 4, 4));
        JTextField tfMName = new JTextField();
        JTextField tfMAmt = new JTextField("10");
        JButton btnConsume = new JButton("Consume");
        JButton btnRemove = new JButton("Remove Ingredient");
        btnConsume.addActionListener(e -> {
            try {
                boolean ok = ctrl.consume(tfMName.getText().trim(),
                    Integer.parseInt(tfMAmt.getText().trim()));
                refreshList();
                log(ok ? "Consumed " + tfMAmt.getText() + " from " + tfMName.getText()
                       : "Not enough stock or ingredient not found");
            } catch (Exception ex) { showError(ex.getMessage()); }
        });
        btnRemove.addActionListener(e -> {
            boolean ok = ctrl.removeIngredient(tfMName.getText().trim());
            refreshList();
            log(ok ? "Removed ingredient: " + tfMName.getText() : "Ingredient not found");
        });
        managePanel.add(new JLabel("Name:")); managePanel.add(tfMName);
        managePanel.add(new JLabel("Amount:")); managePanel.add(tfMAmt);
        managePanel.add(btnConsume); managePanel.add(btnRemove);

        panel.add(addPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(restockPanel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(managePanel);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel buildListPanel() {
        JPanel panel = titledPanel("Inventory");
        panel.setLayout(new BorderLayout(4, 4));

        listModel      = new DefaultListModel<>();
        ingredientList = new JList<>(listModel);
        ingredientList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ingredientList.setCellRenderer(new IngredientRenderer());

        JButton btnRefresh = new JButton("Refresh");
        JButton btnAlerts  = new JButton("Low Stock Alerts");
        btnRefresh.addActionListener(e -> refreshList());
        btnAlerts.addActionListener(e -> showLowStockAlerts());

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(btnRefresh); row.add(btnAlerts);

        panel.add(new JScrollPane(ingredientList), BorderLayout.CENTER);
        panel.add(row, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLogPanel() {
        JPanel p = titledPanel("Log");
        p.setPreferredSize(new Dimension(0, 80));
        p.setLayout(new BorderLayout());
        logArea = new JTextArea(3, 0);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        p.add(new JScrollPane(logArea));
        return p;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void addIngredient() {
        try {
            String name = tfName.getText().trim();
            ctrl.addIngredient(
                name,
                Integer.parseInt(tfQty.getText().trim()),
                tfUnit.getText().trim(),
                Integer.parseInt(tfThreshold.getText().trim())
            );
            refreshList();
            tfName.setText(""); tfQty.setText("100"); tfUnit.setText("g");
            log("Added ingredient: " + name);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void showLowStockAlerts() {
        var low = ctrl.getLowStockAlerts();
        if (low.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All stock levels are OK!", "Stock OK", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("Low Stock Items:\n\n");
        for (Ingredient ing : low) sb.append("  ").append(ing).append("\n");
        JOptionPane.showMessageDialog(this, sb.toString(), "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
    }

    public void refreshList() {
        listModel.clear();
        for (Ingredient ing : ctrl.getAllIngredients()) {
            listModel.addElement(ing.toString());
        }
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel titledPanel(String title) {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), title,
            TitledBorder.LEFT, TitledBorder.TOP));
        return p;
    }

    /** Color-code low-stock rows in red. */
    private static class IngredientRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String text = value.toString();
            if (text.contains("LOW")) {
                setForeground(isSelected ? Color.WHITE : Color.RED);
            }
            return this;
        }
    }
}

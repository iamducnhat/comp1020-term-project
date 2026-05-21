package view;

import controller.CustomerController;
import model.customer.Customer;
import util.SortUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Customer management panel.
 * View: displays customer list, supports add, search, and points management.
 */
public class CustomerPanel extends JPanel {

    private final CustomerController ctrl;

    private DefaultListModel<String> listModel;
    private JList<String>            customerList;
    private JTextField               tfPhone, tfName, tfEmail;
    private JTextField               tfSearch;
    private JLabel                   lblDetails;
    private JTextArea                logArea;

    public CustomerPanel(CustomerController ctrl) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(buildFormPanel(),  BorderLayout.WEST);
        add(buildListPanel(),  BorderLayout.CENTER);
        add(buildLogPanel(),   BorderLayout.SOUTH);

        refreshList();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(240, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // ── Register ─────────────────────────────────────────────────────────
        JPanel reg = titledPanel("Register Customer");
        reg.setLayout(new GridLayout(0, 1, 4, 4));

        tfPhone = new JTextField(); tfName = new JTextField(); tfEmail = new JTextField();
        JButton btnRegister = new JButton("Register");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        btnRegister.addActionListener(e -> registerCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());

        reg.add(new JLabel("Phone*:")); reg.add(tfPhone);
        reg.add(new JLabel("Name*:"));  reg.add(tfName);
        reg.add(new JLabel("Email:"));  reg.add(tfEmail);
        reg.add(btnRegister);
        reg.add(btnUpdate);
        reg.add(btnDelete);

        // ── Search ────────────────────────────────────────────────────────────
        JPanel search = titledPanel("Search by Phone");
        search.setLayout(new GridLayout(0, 1, 4, 4));
        tfSearch = new JTextField();
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchCustomer());
        lblDetails = new JLabel("<html> </html>");
        lblDetails.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        search.add(tfSearch); search.add(btnSearch); search.add(lblDetails);

        // ── Points ────────────────────────────────────────────────────────────
        JPanel pts = titledPanel("Loyalty Points");
        pts.setLayout(new GridLayout(0, 1, 4, 4));
        JTextField tfPtsPhone = new JTextField();
        JTextField tfPtsAmt   = new JTextField("10");
        JButton btnAward  = new JButton("Award Points");
        JButton btnRedeem = new JButton("Redeem Points");
        btnAward.addActionListener(e -> {
            try {
                ctrl.awardPoints(tfPtsPhone.getText().trim(), Integer.parseInt(tfPtsAmt.getText().trim()));
                refreshList(); log("Awarded " + tfPtsAmt.getText() + " pts to " + tfPtsPhone.getText());
            } catch (Exception ex) { showError(ex.getMessage()); }
        });
        btnRedeem.addActionListener(e -> {
            try {
                boolean ok = ctrl.redeemPoints(tfPtsPhone.getText().trim(), Integer.parseInt(tfPtsAmt.getText().trim()));
                refreshList();
                log(ok ? "Redeemed pts" : "Not enough points!");
            } catch (Exception ex) { showError(ex.getMessage()); }
        });
        pts.add(new JLabel("Phone:")); pts.add(tfPtsPhone);
        pts.add(new JLabel("Points:")); pts.add(tfPtsAmt);
        pts.add(btnAward); pts.add(btnRedeem);

        panel.add(reg);
        panel.add(Box.createVerticalStrut(6));
        panel.add(search);
        panel.add(Box.createVerticalStrut(6));
        panel.add(pts);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel buildListPanel() {
        JPanel panel = titledPanel("All Customers");
        panel.setLayout(new BorderLayout(4, 4));

        listModel    = new DefaultListModel<>();
        customerList = new JList<>(listModel);
        customerList.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh    = new JButton("Refresh");
        JButton btnLeaderboard = new JButton("Leaderboard");
        btnRefresh.addActionListener(e -> refreshList());
        btnLeaderboard.addActionListener(e -> showLeaderboard());
        buttonRow.add(btnRefresh); buttonRow.add(btnLeaderboard);

        panel.add(new JScrollPane(customerList), BorderLayout.CENTER);
        panel.add(buttonRow, BorderLayout.SOUTH);
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

    private void registerCustomer() {
        String phone = tfPhone.getText().trim();
        String name  = tfName.getText().trim();
        if (phone.isEmpty() || name.isEmpty()) { showError("Phone and Name required."); return; }
        try {
            Customer c = ctrl.registerCustomer(name, phone);
            if (!tfEmail.getText().isBlank()) c.setEmail(tfEmail.getText().trim());
            refreshList();
            tfPhone.setText(""); tfName.setText(""); tfEmail.setText("");
            log("Registered: " + c);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void updateCustomer() {
        String phone = tfPhone.getText().trim();
        String name = tfName.getText().trim();
        if (phone.isEmpty()) { showError("Phone required."); return; }
        try {
            boolean updated = true;
            if (!name.isEmpty()) updated = ctrl.updateName(phone, name);
            if (!tfEmail.getText().isBlank()) updated = ctrl.updateEmail(phone, tfEmail.getText().trim()) && updated;
            refreshList();
            log(updated ? "Updated customer: " + phone : "Customer not found: " + phone);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void deleteCustomer() {
        String phone = tfPhone.getText().trim();
        if (phone.isEmpty()) { showError("Phone required."); return; }
        boolean removed = ctrl.removeCustomer(phone);
        refreshList();
        log(removed ? "Deleted customer: " + phone : "Customer not found: " + phone);
    }

    private void searchCustomer() {
        String phone = tfSearch.getText().trim();
        Customer c = ctrl.findByPhone(phone);
        if (c == null) {
            lblDetails.setText("<html><b>Not found</b></html>");
        } else {
            lblDetails.setText(String.format(
                "<html><b>%s</b><br>Phone: %s<br>Points: %d %s</html>",
                c.getName(), c.getPhone(), c.getLoyaltyPoints(),
                c.isVip() ? "VIP" : ""));
        }
    }

    private void showLeaderboard() {
        List<Customer> board = ctrl.getLeaderboard();
        StringBuilder sb = new StringBuilder("Loyalty Leaderboard\n\n");
        int rank = 1;
        for (Customer c : board) {
            sb.append(String.format("%-3d %-20s %d pts%s\n",
                rank++, c.getName(), c.getLoyaltyPoints(), c.isVip() ? " VIP" : ""));
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Leaderboard", JOptionPane.PLAIN_MESSAGE);
    }

    public void refreshList() {
        listModel.clear();
        for (Customer c : ctrl.getAllSortedByName()) {
            listModel.addElement(String.format("  %-20s %-14s %3d pts%s",
                c.getName(), c.getPhone(), c.getLoyaltyPoints(), c.isVip() ? " VIP" : ""));
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
}

package view;

import controller.VoucherController;
import model.voucher.Voucher;
import model.voucher.Voucher.VoucherType;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Voucher management panel.
 * Create, view, and validate discount vouchers.
 */
public class VoucherPanel extends JPanel {

    private final VoucherController ctrl;

    private DefaultListModel<String> listModel;
    private JTextField               tfCode, tfValue, tfMinOrder;
    private JComboBox<String>        cbType;
    private JTextField               tfValidateCode, tfValidateAmt;
    private JLabel                   lblValidateResult;
    private JTextArea                logArea;

    public VoucherPanel(VoucherController ctrl) {
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
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // ── Create voucher ────────────────────────────────────────────────────
        JPanel create = titledPanel("Create Voucher");
        create.setLayout(new GridLayout(0, 1, 4, 4));

        tfCode = new JTextField(); tfValue = new JTextField("10");
        tfMinOrder = new JTextField("0");
        cbType = new JComboBox<>(new String[]{"PERCENT", "FIXED"});

        JButton btnCreate = new JButton("Create");
        btnCreate.addActionListener(e -> createVoucher());

        create.add(new JLabel("Code:"));      create.add(tfCode);
        create.add(new JLabel("Type:"));      create.add(cbType);
        create.add(new JLabel("Value:"));     create.add(tfValue);
        create.add(new JLabel("Min Order:")); create.add(tfMinOrder);
        create.add(btnCreate);

        // ── Validate voucher ──────────────────────────────────────────────────
        JPanel validate = titledPanel("Validate Voucher");
        validate.setLayout(new GridLayout(0, 1, 4, 4));

        tfValidateCode = new JTextField(); tfValidateAmt = new JTextField("0");
        lblValidateResult = new JLabel(" ");
        JButton btnValidate = new JButton("Check");
        btnValidate.addActionListener(e -> validateVoucher());

        validate.add(new JLabel("Code:"));         validate.add(tfValidateCode);
        validate.add(new JLabel("Order Total:")); validate.add(tfValidateAmt);
        validate.add(btnValidate);
        validate.add(lblValidateResult);

        JPanel manage = titledPanel("Use / Remove Voucher");
        manage.setLayout(new GridLayout(0, 1, 4, 4));
        JTextField tfManageCode = new JTextField();
        JButton btnMarkUsed = new JButton("Mark Used");
        JButton btnRemove = new JButton("Remove");
        btnMarkUsed.addActionListener(e -> {
            try {
                boolean ok = ctrl.markUsed(tfManageCode.getText().trim());
                refreshList();
                log(ok ? "Marked used: " + tfManageCode.getText() : "Voucher not found");
            } catch (Exception ex) { showError(ex.getMessage()); }
        });
        btnRemove.addActionListener(e -> {
            try {
                boolean ok = ctrl.removeVoucher(tfManageCode.getText().trim());
                refreshList();
                log(ok ? "Removed voucher: " + tfManageCode.getText() : "Voucher not found");
            } catch (Exception ex) { showError(ex.getMessage()); }
        });
        manage.add(new JLabel("Code:")); manage.add(tfManageCode);
        manage.add(btnMarkUsed); manage.add(btnRemove);

        panel.add(create);
        panel.add(Box.createVerticalStrut(6));
        panel.add(validate);
        panel.add(Box.createVerticalStrut(6));
        panel.add(manage);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel buildListPanel() {
        JPanel panel = titledPanel("All Vouchers");
        panel.setLayout(new BorderLayout(4, 4));

        listModel = new DefaultListModel<>();
        JList<String> voucherList = new JList<>(listModel);
        voucherList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        voucherList.setCellRenderer(new VoucherRenderer());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshList());

        panel.add(new JScrollPane(voucherList), BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);
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

    private void createVoucher() {
        try {
            String code     = tfCode.getText().trim();
            VoucherType type = "PERCENT".equals(cbType.getSelectedItem())
                ? VoucherType.PERCENT : VoucherType.FIXED;
            double value    = Double.parseDouble(tfValue.getText().trim());
            double minOrder = Double.parseDouble(tfMinOrder.getText().trim());

            ctrl.createVoucher(code, type, value, minOrder);
            refreshList();
            tfCode.setText(""); tfValue.setText("10"); tfMinOrder.setText("0");
            log("Created voucher: " + code);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void validateVoucher() {
        try {
            String code  = tfValidateCode.getText().trim();
            double total = Double.parseDouble(tfValidateAmt.getText().trim());
            double disc  = ctrl.applyVoucher(code, total);
            if (disc > 0) {
                lblValidateResult.setText(String.format("Discount: -%.0f VND", disc));
                lblValidateResult.setForeground(new Color(0, 130, 0));
                log(String.format("Voucher %s valid: -%.0f VND on %.0f VND order", code, disc, total));
            } else {
                lblValidateResult.setText("Not valid / not applicable");
                lblValidateResult.setForeground(Color.RED);
            }
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    public void refreshList() {
        listModel.clear();
        for (Voucher v : ctrl.getAllVouchers()) {
            listModel.addElement(v.toString());
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

    /** Strike-through style for used vouchers. */
    private static class VoucherRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String text = value.toString();
            if (text.contains("USED")) {
                setForeground(isSelected ? Color.WHITE : Color.GRAY);
                setFont(getFont().deriveFont(Font.ITALIC));
            }
            return this;
        }
    }
}

package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import model.beverage.Beverage;
import model.beverage.Coffee;
import model.beverage.Size;
import model.beverage.Tea;

public class MainFrame extends JFrame {

    private final JTextArea detailArea;

    public MainFrame() {
        setTitle("VinTony Coffee Shop - POS & Loyalty System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Drink Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        DefaultMutableTreeNode rootNode = buildMenuTree();
        JTree menuTree = new JTree(rootNode);
        menuTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        JScrollPane treeScrollPane = new JScrollPane(menuTree);
        treeScrollPane.setPreferredSize(new Dimension(320, 0));
        add(treeScrollPane, BorderLayout.WEST);

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(new JScrollPane(detailArea), BorderLayout.CENTER);

        menuTree.addTreeSelectionListener(event -> updateDetailArea(menuTree));
    }

    private DefaultMutableTreeNode buildMenuTree() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Menu");
        Map<String, Map<String, List<Beverage>>> menuData = getMenuData();

        for (Map.Entry<String, Map<String, List<Beverage>>> categoryEntry : menuData.entrySet()) {
            DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(categoryEntry.getKey());

            for (Map.Entry<String, List<Beverage>> drinkEntry : categoryEntry.getValue().entrySet()) {
                DefaultMutableTreeNode drinkNode = new DefaultMutableTreeNode(drinkEntry.getKey());

                for (Beverage beverage : drinkEntry.getValue()) {
                    drinkNode.add(new DefaultMutableTreeNode(beverage));
                }

                categoryNode.add(drinkNode);
            }

            rootNode.add(categoryNode);
        }

        return rootNode;
    }

    private void updateDetailArea(JTree menuTree) {
        DefaultMutableTreeNode selectedNode =
                (DefaultMutableTreeNode) menuTree.getLastSelectedPathComponent();

        if (selectedNode == null) {
            return;
        }

        Object selectedObject = selectedNode.getUserObject();
        if (selectedObject instanceof Beverage beverage) {
            detailArea.setText(
                    "Selected beverage:\n"
                            + "Type: " + beverage.getClass().getSimpleName() + "\n"
                            + "Name: " + beverage.getName() + "\n"
                            + "Size: " + beverage.getSize() + "\n"
                            + "Price: $" + String.format("%.2f", beverage.calculatePrice()));
            return;
        }

        detailArea.setText("Selected:\n" + selectedObject);
    }

    private Map<String, Map<String, List<Beverage>>> getMenuData() {
        Map<String, Map<String, List<Beverage>>> menuData = new LinkedHashMap<>();

        Map<String, List<Beverage>> coffeeMenu = new LinkedHashMap<>();
        coffeeMenu.put("Espresso", createCoffeeOptions("Espresso", 2.0));
        coffeeMenu.put("Latte", createCoffeeOptions("Latte", 3.0));
        menuData.put("Coffee", coffeeMenu);

        Map<String, List<Beverage>> teaMenu = new LinkedHashMap<>();
        teaMenu.put("Green Tea", createTeaOptions("Green Tea", 1.5));
        menuData.put("Tea", teaMenu);

        return menuData;
    }

    private List<Beverage> createCoffeeOptions(String name, double basePrice) {
        List<Beverage> options = new ArrayList<>();
        for (Size size : Size.values()) {
            options.add(new Coffee(name, size, basePrice));
        }
        return options;
    }

    private List<Beverage> createTeaOptions(String name, double basePrice) {
        List<Beverage> options = new ArrayList<>();
        for (Size size : Size.values()) {
            options.add(new Tea(name, size, basePrice));
        }
        return options;
    }
}
package model.menu;

import model.beverage.Coffee;
import model.beverage.Coffee.CoffeeType;
import model.beverage.Size;
import model.beverage.Tea;
import model.beverage.Tea.TeaType;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds and holds the full menu as a Tree structure.
 *
 * Data Structure: N-ary tree (each node can have unlimited children).
 * Tree traversal is used by the UI to populate JTree / JList.
 *
 * Complexity: O(log n) for tree lookup; O(n) for full traversal.
 */
public class MenuTree {

    private final MenuNode root;

    public MenuTree() {
        root = new MenuNode("Menu");
        buildMenu();
    }

    private void buildMenu() {
        // ── Coffee category ────────────────────────────────────────────────
        MenuNode coffeeCategory = new MenuNode("Coffee");

        for (CoffeeType ct : CoffeeType.values()) {
            MenuNode coffeeNode = new MenuNode(ct.getLabel());
            for (Size size : Size.values()) {
                coffeeNode.addChild(new MenuNode(
                    ct.getLabel() + " (" + size.getLabel() + ")",
                    new Coffee(ct, size)
                ));
            }
            coffeeCategory.addChild(coffeeNode);
        }

        // ── Tea category ────────────────────────────────────────────────────
        MenuNode teaCategory = new MenuNode("Tea");

        for (TeaType tt : TeaType.values()) {
            MenuNode teaNode = new MenuNode(tt.getLabel());
            for (Size size : Size.values()) {
                teaNode.addChild(new MenuNode(
                    tt.getLabel() + " (" + size.getLabel() + ")",
                    new Tea(tt, size)
                ));
            }
            teaCategory.addChild(teaNode);
        }

        root.addChild(coffeeCategory);
        root.addChild(teaCategory);
    }

    public MenuNode getRoot() { return root; }

    /**
     * Flatten all LEAF nodes (actual orderable beverages) via DFS traversal.
     */
    public List<MenuNode> getAllLeaves() {
        List<MenuNode> leaves = new ArrayList<>();
        collectLeaves(root, leaves);
        return leaves;
    }

    private void collectLeaves(MenuNode node, List<MenuNode> result) {
        if (node.isLeaf()) {
            result.add(node);
            return;
        }
        for (MenuNode child : node.getChildren()) {
            collectLeaves(child, result);
        }
    }

    /**
     * Print the full menu tree to stdout — useful for debugging.
     */
    public void printTree() {
        printNode(root, "", true);
    }

    private void printNode(MenuNode node, String prefix, boolean isLast) {
        System.out.println(prefix + (isLast ? "└── " : "├── ") + node.getLabel());
        List<MenuNode> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            printNode(children.get(i),
                prefix + (isLast ? "    " : "│   "),
                i == children.size() - 1);
        }
    }
}

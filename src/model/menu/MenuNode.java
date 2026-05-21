package model.menu;

import model.beverage.Beverage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A node in the hierarchical menu tree.
 *
 * Data Structure: Tree node.
 * Each node is either a CATEGORY (has children, no beverage)
 * or a LEAF (has a Beverage, no children).
 *
 * Example tree:
 *   Menu
 *   ├── Coffee
 *   │   ├── Espresso (S/M/L)
 *   │   └── Latte    (S/M/L)
 *   └── Tea
 *       └── Green Tea (S/M/L)
 */
public class MenuNode {

    private final String       label;
    private final Beverage     beverage;   // null for category nodes
    private final List<MenuNode> children;

    /** Category node constructor. */
    public MenuNode(String label) {
        this.label    = label;
        this.beverage = null;
        this.children = new ArrayList<>();
    }

    /** Leaf node constructor. */
    public MenuNode(String label, Beverage beverage) {
        this.label    = label;
        this.beverage = beverage;
        this.children = new ArrayList<>(); // empty for leaves
    }

    // ── Tree operations ───────────────────────────────────────────────────────
    public void addChild(MenuNode child) {
        children.add(child);
    }

    public List<MenuNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public boolean isLeaf()     { return beverage != null; }
    public boolean isCategory() { return beverage == null; }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String   getLabel()    { return label; }
    public Beverage getBeverage() { return beverage; }

    @Override
    public String toString() { return label; }
}

public class MenuTree {
    private MenuNode root;

    public MenuTree() {
        root = new MenuNode("Menu");
    }

    public MenuNode getRoot() {
        return root;
    }

    // Add category
    public MenuNode addCategory(String name) {
        MenuNode category = new MenuNode(name);
        root.addChild(category);
        return category;
    }

    // Add drink under category
    public void addDrink(MenuNode category, String drinkName) {
        category.addChild(new MenuNode(drinkName));
    }

    // Search drink by name (DFS)
    public MenuNode search(String name) {
        return searchRecursive(root, name);
    }

    private MenuNode searchRecursive(MenuNode node, String name) {
        if (node.getName().equalsIgnoreCase(name)) {
            return node;
        }

        for (MenuNode child : node.getChildren()) {
            MenuNode result = searchRecursive(child, name);
            if (result != null) return result;
        }
        return null;
    }

    // Display menu
    public void display(MenuNode node, int level) {
        System.out.println("  ".repeat(level) + "- " + node.getName());
        for (MenuNode child : node.getChildren()) {
            display(child, level + 1);
        }
    }
}
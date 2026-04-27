import java.util.ArrayList;
import java.util.List;

public class MenuNode {
    private String name;
    private List<MenuNode> children;

    public MenuNode(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addChild(MenuNode child) {
        children.add(child);
    }

    public String getName() {
        return name;
    }

    public List<MenuNode> getChildren() {
        return children;
    }
}
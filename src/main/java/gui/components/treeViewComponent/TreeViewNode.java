package gui.components.treeViewComponent;

public class TreeViewNode {
    private String name;
    private Object node;

    public TreeViewNode(String name, Object node) {
        this.name = name;
        this.node = node;
    }

    public String getName() {
        return name;
    }

    public Object getNode() {
        return node;
    }
}

package sx.shiragane.imdb.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Comparator;

public class ComparableTreeNode extends DefaultMutableTreeNode {
    private final Comparator comparator;

    public ComparableTreeNode(Object userObject, Comparator comparator) {
        super(userObject);
        this.comparator = comparator;
    }

    public ComparableTreeNode(Object userObject) {
        this(userObject, null);
    }

    @Override
    public void add(MutableTreeNode newChild) {
        super.add(newChild);
        if (this.comparator != null) {
            this.children.sort(this.comparator);
        }
    }
}
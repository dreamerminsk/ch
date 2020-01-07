package sx.shirogane;

import com.mongodb.client.MongoCollection;
import sx.shiragone.utils.MongoUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class Viewer extends JFrame {

    private MongoCollection<Player> players = MongoUtils.F_STATS.getCollection("players", Player.class);
    private MongoCollection<Team> teams = MongoUtils.F_STATS.getCollection("teams", Team.class);
    private JTextPane text;
    private JTree tree;

    public Viewer() {
        super("viewer");
        setLayout(new BorderLayout());
        setUi();
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String... args) {
        Viewer viewer = new Viewer();
    }

    private void setUi() {
        text = new JTextPane();
        add(new JScrollPane(text), BorderLayout.CENTER);
        tree = new JTree(new DefaultMutableTreeNode("ROOT"));
        add(new JScrollPane(tree), BorderLayout.CENTER);
        Map<String, DefaultMutableTreeNode> nations = new TreeMap<>();
        teams.find().sort(com.mongodb.client.model.Sorts.ascending("ref")).forEach((Consumer<? super Team>) t -> {
            String nation = t.getNation();
            if (nations.containsKey(nation)) {
                nations.get(nation).add(new DefaultMutableTreeNode(t.getTeam()));
            } else {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(nation);
                node.add(new DefaultMutableTreeNode(t.getTeam()));
                nations.put(nation, node);
                ((MutableTreeNode) tree.getModel().getRoot()).insert(node, 0);
            }
            text.setText(text.getText() + "\r\n" + t.getTeam());
        });
    }

}

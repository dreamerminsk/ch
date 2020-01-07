package sx.shirogane.imdb.ui;

import javax.swing.tree.TreeNode;
import java.time.LocalDate;
import java.time.Year;
import java.util.Map;
import java.util.TreeMap;

public class RootNode extends ComparableTreeNode {

    private Map<Year, YearNode> years = new TreeMap<>();

    public RootNode() {
        super("ROOT", (o1, o2) -> {
            Year y1 = (Year) ((YearNode) o1).getUserObject();
            Year y2 = (Year) ((YearNode) o2).getUserObject();
            return y1.compareTo(y2);
        });
    }

    public TreeNode getYearNode(Year year) {
        return years.get(year);
    }

    public void addDay(LocalDate day, int count) {
        Year y = Year.of(day.getYear());
        if (years.containsKey(y)) {
            YearNode yn = years.get(y);
            yn.addDay(day, count);
        } else {
            years.put(y, new YearNode(y));
            years.get(y).addDay(day, count);
            add(years.get(y));
        }
    }
}

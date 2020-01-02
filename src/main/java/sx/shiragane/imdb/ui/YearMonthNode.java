package sx.shiragane.imdb.ui;

import javax.swing.tree.TreeNode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.TreeMap;

class YearMonthNode extends ComparableTreeNode {

    private Map<LocalDate, YearMonthDayNode> ymds = new TreeMap<>();

    public YearMonthNode(YearMonth ym) {
        super(ym, (o1, o2) -> {
            LocalDate ym1 = (LocalDate) ((YearMonthDayNode) o1).getUserObject();
            LocalDate ym2 = (LocalDate) ((YearMonthDayNode) o2).getUserObject();
            return ym1.compareTo(ym2);
        });
    }

    public void addDay(LocalDate day, int count) {
        if (ymds.containsKey(day)) {
            YearMonthDayNode ymdn = ymds.get(day);
            ymdn.addDay(day, count);
        } else {
            ymds.put(day, new YearMonthDayNode(day, count));
            ymds.get(day).addDay(day, count);
            add(ymds.get(day));
        }
    }

    public TreeNode getYearMonthDayNode(LocalDate day) {
        return ymds.get(day);
    }
}

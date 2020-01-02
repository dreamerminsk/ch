package sx.shiragane.imdb.ui;

import javax.swing.tree.TreeNode;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Map;
import java.util.TreeMap;

class YearNode extends ComparableTreeNode {

    private Map<YearMonth, YearMonthNode> yms = new TreeMap<>();

    public YearNode(Year year) {
        super(year, (o1, o2) -> {
            YearMonth ym1 = (YearMonth) ((YearMonthNode) o1).getUserObject();
            YearMonth ym2 = (YearMonth) ((YearMonthNode) o2).getUserObject();
            return ym1.compareTo(ym2);
        });
    }

    public void addDay(LocalDate day, int count) {
        YearMonth ym = YearMonth.of(day.getYear(), day.getMonthValue());
        if (yms.containsKey(ym)) {
            YearMonthNode ymn = yms.get(ym);
            ymn.addDay(day, count);
        } else {
            yms.put(ym, new YearMonthNode(ym));
            yms.get(ym).addDay(day, count);
            add(yms.get(ym));
        }
    }

    public TreeNode getYearMonthNode(YearMonth yearmonth) {
        return yms.get(yearmonth);
    }
}

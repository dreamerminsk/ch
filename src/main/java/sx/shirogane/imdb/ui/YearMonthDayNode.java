package sx.shirogane.imdb.ui;

import java.time.LocalDate;

public class YearMonthDayNode extends ComparableTreeNode {

    public YearMonthDayNode(LocalDate day, int count) {
        super(day, null);
    }

    public void addDay(LocalDate day, int count) {
    }

}

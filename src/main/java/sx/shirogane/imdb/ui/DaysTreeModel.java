package sx.shirogane.imdb.ui;

import com.alee.laf.optionpane.WebOptionPane;
import com.google.common.collect.ImmutableList;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import sx.shirogane.imdb.model.Movie;
import sx.shirogane.utils.MongoUtils;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DaysTreeModel extends DefaultTreeModel {

    private MongoCollection<Movie> imdbTitles;

    public DaysTreeModel() {
        super(new RootNode());
        CompletableFuture.supplyAsync(MongoUtils::getImdbTitles)

                .thenAccept(this::init).thenRun(() -> {

        });
    }

    private void init(MongoCollection<Movie> coll) {
        imdbTitles = coll;
        AggregateIterable<Document> days = getAggregate();
        List<Document> daysIt = new ArrayList<>();
        days.forEach((Consumer<? super Document>) daysIt::add);
        WebOptionPane.showMessageDialog(null, "Days: " + daysIt.size());
        daysIt.stream()
                .filter(d -> d.get("_id") != null)
                .map(d -> {
                    return Instant.ofEpochMilli(((Date) d.get("_id")).getTime())
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate();
                })
                .forEach(d -> ((RootNode) getRoot()).addDay(d, 0));
    }

    @NotNull
    private AggregateIterable<Document> getAggregate() {
        return imdbTitles.aggregate(
                ImmutableList.of(
                        Aggregates.group("$release", Accumulators.sum("count", 1)),
                        Aggregates.sort(Sorts.ascending("_id"))
                ),
                Document.class);
    }

    public TreeNode getYearNode(Year year) {
        return ((RootNode) getRoot()).getYearNode(year);
    }

    public TreeNode getYearMonthNode(YearMonth yearmonth) {
        return ((YearNode) getYearNode(Year.of(yearmonth.getYear()))).getYearMonthNode(yearmonth);
    }

    public TreeNode getYearMonthDayNode(LocalDate day) {
        return ((YearMonthNode) getYearMonthNode(YearMonth.of(day.getYear(), day.getMonthValue()))).getYearMonthDayNode(day);
    }

    private static class DayStats {
        private Date _id;
        private Integer count;

        public DayStats() {

        }

        public Date get_id() {
            return _id;
        }

        public void set_id(Date _id) {
            this._id = _id;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

}

package sx.shiragane.imdb;


import com.alee.laf.WebLookAndFeel;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import sx.shiragane.imdb.model.Movie;
import sx.shiragane.imdb.ui.DaysTreeModel;
import sx.shiragane.imdb.ui.YearMonthDayNode;
import sx.shiragane.utils.MongoUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Consumer;

public class IMDbViewer extends JFrame implements TreeSelectionListener {
    private DefaultListModel<Movie> titlesModel = new DefaultListModel<>();
    private JList<Movie> titlesList = new JList<>(titlesModel);
    private MongoCollection<Movie> imdbTitles = MongoUtils.IMDB_TITLES;
    private DaysTreeModel daysModel = new DaysTreeModel();
    private JTree daysTree = new JTree(daysModel);
    private LocalDate currentDate = LocalDate.now();

    public IMDbViewer() {
        super("IMDbViewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setupUI();
        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String... args) {
        SwingUtilities.invokeLater(() -> {
            WebLookAndFeel.install();
            IMDbViewer v = new IMDbViewer();
        });
    }

    private void setupUI() {
        //TreeNode yearMonthDayNode = daysModel.getYearMonthDayNode(LocalDate.now());
        //daysTree.expandPath(new TreePath(yearMonthDayNode));
        daysTree.expandRow(0);
        daysTree.setRootVisible(false);
        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(daysTree),
                new JScrollPane(titlesList)), BorderLayout.CENTER);
        daysTree.addTreeSelectionListener(this);
    }

    private void updateTitles() {
        SwingUtilities.invokeLater(() -> {
            titlesModel.clear();
        });
        imdbTitles.find(Filters.eq("release", Date.from(currentDate.atStartOfDay().atZone(ZoneId.of("UTC")).toInstant())))
                .forEach((Consumer<? super Movie>) r -> {
                    SwingUtilities.invokeLater(() -> {
                        titlesModel.addElement(r);
                    });
                });
        SwingUtilities.invokeLater(() -> {
            titlesList.revalidate();
            titlesList.repaint();
        });
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        Object lpc = daysTree.getSelectionPath().getLastPathComponent();
        if (lpc.getClass().isAssignableFrom(YearMonthDayNode.class)) {
            YearMonthDayNode ymdn = (YearMonthDayNode) lpc;
            LocalDate ld = (LocalDate) ymdn.getUserObject();
            if (!currentDate.isEqual(ld)) {
                currentDate = ld;
                SwingUtilities.invokeLater(this::updateTitles);
            }
        }
    }
}

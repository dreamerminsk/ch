package sx.shirogane.imdb;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.WebLookAndFeel;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.imgscalr.Scalr;
import sx.shirogane.imdb.model.Movie;
import sx.shirogane.imdb.ui.DaysTreeModel;
import sx.shirogane.imdb.ui.YearMonthDayNode;
import sx.shirogane.utils.MongoUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Consumer;

import static com.alee.extended.layout.VerticalFlowLayout.TOP;

public class IMDbViewer extends JFrame implements TreeSelectionListener {
    
    private static final String VERSION = "v2020-01-07";
    
    private DefaultListModel<Movie> titlesModel = new DefaultListModel<>();
    private JPanel titlesPanel = new JPanel(new VerticalFlowLayout(TOP, 5, 5));
    private JList<Movie> titlesList = new JList<>(titlesModel);
    private MongoCollection<Movie> imdbTitles = MongoUtils.getImdbTitles();
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
                new JScrollPane(titlesPanel)), BorderLayout.CENTER);
        daysTree.addTreeSelectionListener(this);
    }

    private void updateTitles() {
        SwingUtilities.invokeLater(() -> {
            titlesModel.clear();
            titlesPanel.removeAll();
        });
        imdbTitles.find(Filters.eq("release", Date.from(currentDate.atStartOfDay().atZone(ZoneId.of("UTC")).toInstant())))
                .forEach((Consumer<? super Movie>) r -> {
                    SwingUtilities.invokeLater(() -> {
                        titlesModel.addElement(r);
                        titlesPanel.add(new JLabel(r.getTitle()));
                    });
                });
        SwingUtilities.invokeLater(() -> {
            titlesList.revalidate();
            titlesList.repaint();
            titlesPanel.revalidate();
            titlesPanel.repaint();
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

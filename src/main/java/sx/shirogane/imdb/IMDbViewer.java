package sx.shirogane.imdb;

import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.tree.WebTree;
import com.mongodb.client.model.Filters;
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

    private JPanel titlesPanel = new WebPanel(new VerticalFlowLayout(TOP, 5, 5));
    private DaysTreeModel daysModel = new DaysTreeModel();
    private JTree daysTree = new WebTree(daysModel);
    private LocalDate currentDate = LocalDate.now();

    public IMDbViewer() {
        super("IMDbViewer " + VERSION);
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
        daysTree.setRootVisible(true);
        JScrollPane newRightComponent = new JScrollPane(titlesPanel);
        newRightComponent.getVerticalScrollBar().setBlockIncrement(64);
        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(daysTree),
                newRightComponent), BorderLayout.CENTER);
        daysTree.addTreeSelectionListener(this);
    }

    private void updateTitles() {
        SwingUtilities.invokeLater(() -> {
            titlesPanel.removeAll();
        });
        MongoUtils.getImdbTitles().find(Filters.eq("release", Date.from(currentDate.atStartOfDay().atZone(ZoneId.of("UTC")).toInstant())))
                .forEach((Consumer<? super Movie>) r -> {
                    SwingUtilities.invokeLater(() -> {
                        JLabel comp = new JLabel(r.getTitle() + r.getYear());
                        comp.setFont(comp.getFont().deriveFont(18f));
                        titlesPanel.add(comp);
                    });
                });
        SwingUtilities.invokeLater(() -> {
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

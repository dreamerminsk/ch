package sx.shiragane.imdb;


import com.alee.extended.layout.CompactFlowLayout;
import com.alee.extended.layout.VerticalFlowLayout;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.managers.style.StyleId;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.imgscalr.Scalr;
import sx.shiragane.imdb.model.Movie;
import sx.shiragane.imdb.ui.DaysTreeModel;
import sx.shiragane.imdb.ui.YearMonthDayNode;
import sx.shiragane.utils.MongoUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.alee.extended.layout.VerticalFlowLayout.TOP;

public class IMDbViewer extends JFrame implements TreeSelectionListener {
    private DefaultListModel<Movie> titlesModel = new DefaultListModel<>();
    private JPanel titlesPanel = new JPanel(new VerticalFlowLayout(TOP, 7, 7));
    private JList<Movie> titlesList = new JList<>(titlesModel);
    private MongoCollection<Movie> imdbTitles = MongoUtils.getImdbTitles();
    private DaysTreeModel daysModel = new DaysTreeModel();
    private JTree daysTree = new JTree(daysModel);
    private LocalDate currentDate = LocalDate.now();
    private WebButton reload;

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
        daysTree.setRootVisible(true);
        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(daysTree),
                new JScrollPane(titlesPanel)), BorderLayout.CENTER);
        daysTree.addTreeSelectionListener(this);
        reload = new WebButton("RELOAD");
        reload.addActionListener(e -> {
            daysModel = new DaysTreeModel();
            daysTree.setModel(daysModel);
        });
        add(reload, BorderLayout.NORTH);
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
                        WebPanel content = new WebPanel(new CompactFlowLayout(FlowLayout.LEADING, 7, 7));
                        JLabel comp = new JLabel(r.getTitle() + " " + r.getYear());
                        CompletableFuture.supplyAsync(() -> {
                            try {
                                return ImageIO.read(new URL(r.getPoster()));
                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }).thenApply(new Function<BufferedImage, BufferedImage>() {
                            @Override
                            public BufferedImage apply(BufferedImage bufferedImage) {
                                return Scalr.resize(
                                        bufferedImage,
                                        Scalr.Method.ULTRA_QUALITY,
                                        Scalr.Mode.FIT_TO_WIDTH,
                                        128,
                                        128);
                            }
                        }).thenAccept(icon -> {
                            SwingUtilities.invokeLater(() -> {
                                comp.setIcon(new ImageIcon(icon));
                                comp.repaint();
                            });
                        });
                        content.add(comp);
                        content.setMargin(7);
                        content.setStyleId(StyleId.panelDecorated);
                        titlesPanel.add(content);
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

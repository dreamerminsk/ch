package sx.shirogane.imdb.ui;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.layout.CompactFlowLayout;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import org.imgscalr.Scalr;
import sx.shirogane.imdb.model.Movie;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public class TitlePanel extends WebPanel {

    private final Movie movie;
    private WebLabel title;
    private WebLabel year;
    private WebLabel desc;
    private WebPanel genres;
    private WebDecoratedImage pic;

    public TitlePanel(Movie movie) {
        super(new GridBagLayout());
        this.movie = movie;
        setupUi();
    }

    private void setupUi() {
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createTitledBorder("")));
        GridBagConstraints gbc = new GridBagConstraints();

        pic = new WebDecoratedImage();
        pic.setDrawGlassLayer(true, true);
        pic.setRound(5);
        pic.setShadeWidth(5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.insets = new Insets(4, 4, 4, 4);
        add(pic, gbc);

        title = new WebLabel();
        title.setFontSize(16);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        add(title, gbc);

        year = new WebLabel();
        year.setFontSizeAndStyle(14, Font.ITALIC);
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(year, gbc);

        genres = new WebPanel(new CompactFlowLayout(FlowLayout.LEFT, 5, 5));
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        add(genres, gbc);

        desc = new WebLabel();
        desc.setFontSize(12);
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        add(desc, gbc);

        update();
    }

    private void update() {
        if (movie.getTitle() != null) {
            title.setText(movie.getTitle());
        }
        if (movie.getYear() != null) {
            year.setText("" + movie.getYear() + " ");
        }
        if (movie.getDescription() != null) {
            desc.setText(movie.getDescription());
        }
        movie.getGenres().stream().map(t -> {
            WebLabel l = new WebLabel(t + " ");
            l.setFontSizeAndStyle(13, Font.ITALIC);
            return l;
        }).forEach(l -> {
            genres.add(l);
        });
        CompletableFuture.supplyAsync(() -> Objects.requireNonNull(loadPic(movie.getPoster())))
                .thenApply(p -> Scalr.resize(p, 128, 128))
                .thenAccept(p -> {
                    SwingUtilities.invokeLater(() -> {
                        pic.setIcon(new ImageIcon(p));
                        pic.revalidate();
                        pic.repaint();
                    });
                });
    }

    private BufferedImage loadPic(String poster) {
        try {
            URL url = new URL(poster);
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

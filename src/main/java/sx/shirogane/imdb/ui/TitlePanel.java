package sx.shirogane.imdb.ui;

import com.alee.extended.layout.CompactFlowLayout;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import sx.shirogane.imdb.model.Movie;

import javax.swing.*;
import java.awt.*;


public class TitlePanel extends WebPanel {

    private final Movie movie;
    private WebLabel title;
    private WebLabel year;
    private WebLabel desc;
    private WebPanel genres;

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

        title = new WebLabel();
        title.setFontSize(16);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        add(title, gbc);

        year = new WebLabel();
        year.setFontSizeAndStyle(14, Font.ITALIC);
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(year, gbc);

        genres = new WebPanel(new CompactFlowLayout(FlowLayout.LEFT, 5, 5));
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        add(genres, gbc);

        desc = new WebLabel();
        desc.setFontSize(12);
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        add(desc, gbc);

        update();
    }

    private void update() {
        title.setText(movie.getTitle());
        year.setText("" + movie.getYear() + " ");
        desc.setText(movie.getDescription());
        movie.getGenres().stream().map(t -> {
            WebLabel l = new WebLabel(t + " ");
            l.setFontSizeAndStyle(13, Font.ITALIC);
            return l;
        }).forEach(l -> {
            genres.add(l);
        });
    }

}

package sx.shirogane.imdb.ui;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import sx.shirogane.imdb.model.Movie;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends WebPanel {

    private final Movie movie;
    private WebLabel title;
    private WebLabel year;

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
        add(title, gbc);

        year = new WebLabel();
        year.setFontSize(14);
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(year, gbc);

        update();
    }

    private void update() {
        title.setText(movie.getTitle());
        year.setText(movie.getYear());
    }

}

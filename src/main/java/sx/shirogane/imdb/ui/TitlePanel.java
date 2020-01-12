package sx.shirogane.imdb.ui;

import com.alee.laf.panel.WebPanel;
import sx.shirogane.imdb.model.Movie;

import java.awt.*;

public class TitlePanel extends WebPanel {

    private final Movie movie;

    public TitlePanel(Movie movie) {
        super(new GridBagLayout());
        this.movie = movie;
        setUi();
    }
    private void setupUi() {
        
        update();
 }

    private void update() {
    }

}

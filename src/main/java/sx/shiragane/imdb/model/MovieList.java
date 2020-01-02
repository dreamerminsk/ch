package sx.shiragane.imdb.model;

import java.util.ArrayList;
import java.util.List;

public class MovieList {

    private List<Movie> movies = new ArrayList<>();

    private String next = "";

    public MovieList() {

    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}

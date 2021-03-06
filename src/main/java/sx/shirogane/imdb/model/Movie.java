package sx.shirogane.imdb.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Movie {

    private ObjectId id;
    private int IMDbID;
    private String title = "...Unknown Title...";
    private String year = "....";
    private List<String> genres = new ArrayList<>();
    private String description = "...";
    private String poster;
    private String status = "...";
    private List<Integer> directors = new ArrayList<>();
    private List<Integer> stars = new ArrayList<>();
    private List<String> countries = new ArrayList<>();
    private LocalDate release;
    private Map<String, LocalDate> releases = new TreeMap<>();

    public Movie() {

    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getIMDbID() {
        return IMDbID;
    }

    public void setIMDbID(int IMDbID) {
        this.IMDbID = IMDbID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Integer> getDirectors() {
        return directors;
    }

    public void setDirectors(List<Integer> directors) {
        this.directors = directors;
    }

    public List<Integer> getStars() {
        return stars;
    }

    public void setStars(List<Integer> stars) {
        this.stars = stars;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public LocalDate getRelease() {
        return release;
    }

    public void setRelease(LocalDate release) {
        this.release = release;
    }

    public String toString() {
        String text = title;
        if (year != null) {
            text += MessageFormat.format(" / {0}", year);
        }
        if (countries != null) {
            text += MessageFormat.format(" / {0}", String.join(", ", countries));
        }
        if (genres != null) {
            text += MessageFormat.format(" / {0}", String.join(", ", genres));
        }
        return text;
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Movie)) return false;
        final Movie that = (Movie) other;
        return this.getIMDbID() == that.getIMDbID();
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "");
    }
}

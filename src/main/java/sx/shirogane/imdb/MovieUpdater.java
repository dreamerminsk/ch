package sx.shirogane.imdb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.jsoup.nodes.Document;
import sx.shirogane.imdb.model.Movie;
import sx.shirogane.utils.MongoUtils;
import sx.shirogane.utils.OkUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

public class MovieUpdater {

    public static void main(String... args) {
        MongoCollection<Movie> titles = MongoUtils.getImdbTitles();
        //titles.find(and(eq("release", null), eq("year", "2020")))
        titles.find(eq("year", "2020"))
                .forEach((Consumer<? super Movie>) movie -> {
            String ref = "https://www.imdb.com/title/tt" + movie.getIMDbID();
            try {
                Document doc = OkUtils.getPage(ref);
                movie.setCountries(
                        doc.select("div#titleDetails a").stream()
                                .filter(el -> el.attr("href").contains("country_of_origin="))
                                .map(el -> el.text().trim())
                                .collect(Collectors.toList()));
                doc.select("div.poster img").forEach(img->{
                    movie.setPoster(img.attr("src"));
                });
                doc.select("div.subtext a").stream()
                        .filter(el -> el.attr("href").contains("releaseinfo"))
                        .map(el -> {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
                            String trim = el.text().trim();
                            String[] dts = trim.split("\\(");
                            try {
                                return LocalDate.parse(dts[0].trim(), formatter);
                            } catch (Exception e) {
                                return null;
                            }
                        }).filter(Objects::nonNull).findFirst().ifPresent(movie::setRelease);
                titles.replaceOne(Filters.eq("iMDbID", movie.getIMDbID()), movie);
                System.out.println("\t\t\t" + movie.getTitle() + " " + movie.getYear() + " / " + movie.getRelease());
                if (movie.getGenres() != null) {
                    System.out.println("\t\t\t" + String.join(", ", movie.getGenres()));
                }
                if (movie.getDirectors() != null) {
                    System.out.println("\t\t\t" + movie.getDirectors().stream()
                            .map(it -> Integer.toString(it)).collect(Collectors.joining(", ")));
                }
                if (movie.getStars() != null) {
                    System.out.println("\t\t\t" + movie.getStars().stream()
                            .map(it -> Integer.toString(it)).collect(Collectors.joining(", ")));
                }
                Thread.sleep(4000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}

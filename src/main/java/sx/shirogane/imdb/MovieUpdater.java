package sx.shirogane.imdb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.jsoup.nodes.Document;
import sx.shirogane.imdb.model.Movie;
import sx.shirogane.imdb.parsers.TitleParser;
import sx.shirogane.utils.MongoUtils;
import sx.shirogane.utils.OkUtils;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

public class MovieUpdater {

    public static void main(String... args) {
        MongoCollection<Movie> titles = MongoUtils.getImdbTitles();
        titles.find(Filters.and(eq("release", null), eq("year", "2020")))
        //titles.find(eq("year", "2020"))
                .forEach((Consumer<? super Movie>) movie -> {
            String ref = "https://www.imdb.com/title/tt" + movie.getIMDbID();
            try {
                Document doc = OkUtils.getPage(ref);
                Movie parsedMovie = TitleParser.parse(doc);
                doc.select("div.poster img").forEach(img->{
                    parsedMovie.setPoster(img.attr("src"));
                });
                titles.replaceOne(Filters.eq("iMDbID", movie.getIMDbID()), parsedMovie);
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

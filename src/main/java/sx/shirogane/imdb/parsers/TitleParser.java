package sx.shirogane.imdb.parsers;

import org.jsoup.nodes.Document;
import sx.shirogane.imdb.model.Movie;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class TitleParser {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

    public static Movie parse(Document doc) {
        Movie movie = new Movie();
        movie.setCountries(parseCountries(doc));
        doc.select("div.subtext a").stream()
            .filter(el - > el.attr("href").contains("releaseinfo"))
            .map(el - > {
                String trim = el.text().trim();
                String[] dts = trim.split("\\(");
                try {
                    return LocalDate.parse(dts[0].trim(), formatter);
                } catch (Exception e) {
                    return null;
                }
            }).filter(Objects::nonNull).findFirst().ifPresent(movie::setRelease);
        return movie;
    }
    
    private static List<String> parseCountries(Document doc) {
        return doc.select("div#titleDetails a").stream()
            .filter(el - > el.attr("href").contains("country_of_origin="))
            .map(el - > el.text().trim())
            .collect(Collectors.toList())
    }

}

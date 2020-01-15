package sx.shirogane.imdb.parsers;

import org.jsoup.nodes.Document;
import sx.shirogane.imdb.model.Movie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class TitleParser {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

    public static Movie parse(Document doc) {
        Movie movie = new Movie();
        parseTitle(doc).ifPresent(movie::setTitle);
        parseYear(doc).ifPresent(movie::setYear);
        movie.setCountries(parseCountries(doc));
        parseRelease(doc).ifPresent(movie::setRelease);
        parsePoster(doc).ifPresent(movie::setPoster);
        return movie;
    }

    private static Optional<String> parseTitle(Document doc) {
        return doc.select("div.title_wrapper > h1").stream().map(el -> el.ownText().trim()).findFirst();
    }

    private static Optional<LocalDate> parseRelease(Document doc) {
        return doc.select("div.subtext a").stream()
                .filter(el -> el.attr("href").contains("releaseinfo"))
                .map(el -> {
                    String trim = el.text().trim();
                    String[] dts = trim.split("\\(");
                    try {
                        return LocalDate.parse(dts[0].trim(), formatter);
                    } catch (Exception e) {
                        return null;
                    }
                }).filter(Objects::nonNull).findFirst();
    }

    private static Optional<String> parseYear(Document doc) {
        return doc.select("span#titleYear a").stream().map(el -> el.text().trim()).findFirst();
    }

    private static List<String> parseCountries(Document doc) {
        return doc.select("div#titleDetails a").stream()
                .filter(el -> el.attr("href").contains("country_of_origin="))
                .map(el -> el.text().trim())
                .collect(Collectors.toList());
    }

    private static Optional<String> parsePoster(Document doc) {
        return doc.select("div.poster img").stream().map(el-> el.attr("src")).findFirst();
    }

}

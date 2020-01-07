package sx.shiragane.imdb.parsers;

import org.jsoup.nodes.Document;
import sx.shiragane.imdb.model.Movie;

public class TitleParser {

    public static Movie parse(Document doc) {
        Movie movie = new Movie();
        movie.setCountries(
                        doc.select("div#titleDetails a").stream()
                                .filter(el -> el.attr("href").contains("country_of_origin="))
                                .map(el -> el.text().trim())
                                .collect(Collectors.toList()));
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
        return movie;
    }

}

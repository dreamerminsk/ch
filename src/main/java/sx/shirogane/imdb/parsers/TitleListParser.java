package sx.shirogane.imdb.parsers;

import org.jsoup.nodes.Document;
import sx.shiragane.imdb.model.Movie;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class TitleListParser {

    public static List < Movie > parse(Document doc) {
        List < Movie > movies = new ArrayList < > ();
        movies.addAll(doc.select("div.lister-item").stream().map(TitleListParser::parseMovie).collect(Collectors.toList()));
        return movies;
    }

    private static Movie parseMovie(Element el) {
        Movie movie = new Movie();
        el.select(".lister-item-header a").forEach(elem - > {
            String href = elem.attr("href");
            Stream.of(href.split("/")).filter(p - > p.startsWith("tt"))
            .map(p - > p.substring(2))
            .mapToInt(Integer::parseInt)
            .forEachOrdered(movie::setIMDbID);
            movie.setTitle(elem.ownText().trim());
        });
        el.select(".lister-item-year").forEach(elem - > {
            try {
                movie.setYear(elem.ownText().trim().substring(1, 5));
            } catch (Exception e) {
                movie.setYear("????");
            }
        });
        el.select(".genre").forEach(elem - > {
            String ownText = elem.ownText().trim();
            movie.setGenres(Stream.of(ownText.split(",")).map(String::trim).collect(Collectors.toList()));
        });
        el.select("div.lister-item-content p.text-muted").forEach(elem - > {
            String ownText = elem.text().trim();
            System.out.println("\t" + ownText);
            movie.setDescription(ownText);
        });
        el.select("div.lister-item-image img").forEach(elem - > {
            movie.setPoster(elem.attr("src"));
        });
        el.select("p.text-muted b").forEach(elem - > {
            movie.setStatus(elem.text().trim());
        });
        el.select("div.lister-item-content p[class='']").forEach(elem - > {
            final List < Integer > ds = new ArrayList < Integer > ();
            final List < Integer > ss = new ArrayList < Integer > ();
            AtomicBoolean isSpan = new AtomicBoolean(false);
            elem.children().forEach(c - > {
                if (c.nodeName().equalsIgnoreCase("span")) {
                    isSpan.set(true);
                } else if (c.nodeName().equalsIgnoreCase("a")) {
                    Name name = Name.of(c);
                    insertName(name);
                    if (isSpan.get()) {
                        ss.add(name.getImdbId());
                    } else {
                        ds.add(name.getImdbId());
                    }
                }
                movie.setDirectors(ds);
                movie.setStars(ss);
            });
        });
        return movie;
    }

}

package sx.shiragane.imdb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import sx.shiragane.imdb.model.Movie;
import sx.shiragane.imdb.model.MovieList;
import sx.shiragane.imdb.model.Name;
import sx.shiragane.utils.MongoUtils;
import sx.shiragane.utils.OkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieListParser {

    private static String ref = "https://www.imdb.com/search/title/?year=2020&title_type=feature,tv_series,mini_series&ref_=tt_ov_inf";

    public MovieListParser() {

    }

    public static void main(String... args) throws IOException {
        MongoCollection<Movie> titles = MongoUtils.IMDB_TITLES;
        MovieListParser p = new MovieListParser();
        String next = ref;
        while (true) {
            MovieList movieList = p.parse(next);
            movieList.getMovies().forEach(m -> {
                System.out.println(m.getTitle());
                if (titles.find(Filters.eq("iMDbID", m.getIMDbID())).first() != null) {
                    try {
                        System.out.println("\tUPDATE:" + m.getTitle());
                        titles.replaceOne(Filters.eq("iMDbID", m.getIMDbID()), m);
                    } catch (Exception ex) {
                    }
                } else {
                    try {
                        titles.insertOne(m);
                    } catch (Exception e) {

                    }
                }
            });
            if (!movieList.getNext().isEmpty()) {
                next = movieList.getNext();
            } else {
                break;
            }
        }
    }

    public MovieList parse(String r) throws IOException {
        Document doc = OkUtils.getPage(r);
        MovieList ml = new MovieList();
        ml.setNext(doc.select("a.lister-page-next").first().attr("abs:href"));
        ml.setMovies(doc.select("div.lister-item").stream().map(this::parseMovie).collect(Collectors.toList()));
        return ml;
    }

    private Movie parseMovie(Element el) {
        Movie movie = new Movie();
        el.select(".lister-item-header a").forEach(elem -> {
            String href = elem.attr("href");
            Stream.of(href.split("/")).filter(p -> p.startsWith("tt"))
                    .map(p -> p.substring(2))
                    .mapToInt(Integer::parseInt)
                    .forEachOrdered(movie::setIMDbID);
            movie.setTitle(elem.ownText().trim());
        });
        el.select(".lister-item-year").forEach(elem -> {
            try {
                movie.setYear(elem.ownText().trim().substring(1, 5));
            } catch (Exception e) {
                movie.setYear("????");
            }
        });
        el.select(".genre").forEach(elem -> {
            String ownText = elem.ownText().trim();
            movie.setGenres(Stream.of(ownText.split(",")).map(String::trim).collect(Collectors.toList()));
        });
        el.select("div.lister-item-content p.text-muted").forEach(elem -> {
            String ownText = elem.text().trim();
            System.out.println("\t" + ownText);
            movie.setDescription(ownText);
        });
        el.select("div.lister-item-image img").forEach(elem -> {
            movie.setPoster(elem.attr("src"));
        });
        el.select("p.text-muted b").forEach(elem -> {
            movie.setStatus(elem.text().trim());
        });
        el.select("div.lister-item-content p[class='']").forEach(elem -> {
            final List<Integer> ds = new ArrayList<Integer>();
            final List<Integer> ss = new ArrayList<Integer>();
            AtomicBoolean isSpan = new AtomicBoolean(false);
            elem.children().forEach(c -> {
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

    private void insertName(Name name) {
        try {
            MongoUtils.IMDB_NAMES.insertOne(name);
        } catch (Exception e) {

        }
    }
}

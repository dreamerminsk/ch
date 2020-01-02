package sx.shiragane.imdb.model;

import org.bson.types.ObjectId;
import org.jsoup.nodes.Element;

import java.util.stream.Stream;

public class Name {

    private ObjectId id;
    private int imdbId;
    private String name;

    public Name() {

    }

    public static Name of(Element el) {
        Name nm = new Name();
        nm.name = el.text().trim();
        String href = el.attr("href");
        nm.imdbId = Stream.of(href.split("/"))
                .filter(t -> t.startsWith("nm"))
                .map(t -> t.substring(2))
                .mapToInt(Integer::valueOf).reduce(0, (left, right) -> right);
        return nm;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getImdbId() {
        return imdbId;
    }

    public void setImdbId(int imdbId) {
        this.imdbId = imdbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

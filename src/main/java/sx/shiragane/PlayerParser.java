package sx.shiragane;

import com.mongodb.client.MongoCollection;
import org.jsoup.nodes.Document;
import sx.shiragane.utils.MongoUtils;

import java.io.IOException;
import java.util.stream.Collectors;

import static sx.shiragane.utils.OkUtils.getPage;

public class PlayerParser {

    public static void main(String... args) throws IOException {
        Document doc = getPage("https://www.championat.com/football/_other/tournament/3103/table/");
        doc.select("a[href]").stream()
                .filter(el -> el.attr("href").contains("/teams/"))
                .filter(el -> !el.attr("href").endsWith("/teams/"))
                .map(el -> el.attr("href"))
                .map(t -> t.replace("result", "players"))
                .map(t -> "https://www.championat.com" + t)
                .collect(Collectors.toSet())
                .forEach(t -> {
                    System.out.println("----------------------");
                    System.out.println(t);
                    parsePlayers(t);
                });
    }

    private static void parsePlayers(String ref) {
        try {
            Document doc = getPage(ref);
            doc.select("a[href]").stream()
                    .filter(el -> {
                        return el.attr("href").contains("/players/");
                    })
                    .filter(el -> {
                        return !el.attr("href").endsWith("/players/");
                    })
                    .forEach(el -> {
                        Player p = new Player();
                        p.setName(el.text());
                        p.setRef(el.attr("href"));
                        MongoCollection<Player> players = MongoUtils.F_STATS.getCollection("players", Player.class);
                        try {
                            System.out.println(p.getRef());
                            players.insertOne(p);
                        } catch (Exception ignored) {
                            System.out.println("\tSKIP");
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

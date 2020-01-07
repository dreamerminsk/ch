package sx.shirogane;

import com.mongodb.client.MongoCollection;
import org.joda.time.DateTime;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import sx.shirogane.utils.MongoUtils;
import sx.shirogane.utils.OkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class PlayerUpdater {

    public static void main(String... args) {
        MongoCollection<Player> players = MongoUtils.F_STATS.getCollection("players", Player.class);
        MongoCollection<Team> teams = MongoUtils.F_STATS.getCollection("teams", Team.class);
        players.find(eq("image", null)).forEach((Consumer<? super Player>) p -> {
            //players.find().forEach((Consumer<? super Player>) p -> {
            System.out.println(p.getRef());
            String ref = "https://www.championat.com" + p.getRef();
            try {
                Document doc = OkUtils.getPage(ref);
                List<Element> lis = new ArrayList<>(doc.select("ul.tournament-header__facts[data-type='player'] li.tournament-header__facts-number"));
                doc.select("div.tournament-header__img img").stream().limit(1)
                        .forEach(el -> {
                            try {
                                players.updateOne(eq("ref", p.getRef()), set("image", el.attr("src")));
//                                GridFS gfsPhoto = new GridFS((DB) MongoUtils.database, "players");
//                                GridFSInputFile image = gfsPhoto.createFile(OkUtils.getImage(el.attr("src")));
//                                image.setFilename(p.getName());
//                                image.save();
//                                System.out.println("\t\t" + image.getId() +  "\t///\t" + image.getLength());
                            } catch (Exception e) {
                                System.out.println("\t\t\t" + e.getMessage());
                            }
                        });
                doc.select("div.tournament-header__title-name").stream().limit(1)
                        .forEach(el -> {
                            try {
                                players.updateOne(eq("ref", p.getRef()), set("short_name", el.text().trim()));
                            } catch (Exception e) {
                            }
                        });
                doc.select("ul.tournament-header__facts[data-type='player'] > li:nth-child(" + (lis.size() + 2) + ")")
                        .forEach(el -> {
                            try {
                                players.updateOne(eq("ref", p.getRef()), set("amplua", el.ownText().trim()));
                            } catch (Exception e) {
                            }
                        });
                doc.select("ul.tournament-header__facts[data-type='player'] > li:nth-child(" + (lis.size() + 3) + ")")
                        .forEach(el -> {
                            try {
                                players.updateOne(eq("ref", p.getRef()), set("nation", el.ownText().trim()));
                            } catch (Exception e) {
                            }
                        });
                doc.select("ul.tournament-header__facts[data-type='player'] > li:nth-child(" + (lis.size() + 4) + ")")
                        .forEach(el -> {
                            try {
                                String[] parts = el.ownText().trim().split("\\.");
                                DateTime bd = DateTime.parse(parts[2] + "-" + parts[1] + "-" + parts[0] + "T00:00:00Z");
                                players.updateOne(eq("ref", p.getRef()), set("birth",
                                        bd.toDate()));
                            } catch (Exception e) {
                                System.out.println("\t\t\tERROR" + e.getMessage());
                            }
                        });
                doc.select("ul.tournament-header__facts li a")
                        .stream()
                        .filter(el -> el.attr("href").contains("/teams/"))
                        .peek(el -> System.out.println(el.text()))
                        .map(el -> {
                            Team t = new Team();
                            t.setTeam(el.text().trim());
                            t.setRef(el.attr("href"));
                            System.out.println("\t\t" + t.getRef());
                            return t;
                        })
                        .forEachOrdered(t -> {
                            try {
                                teams.insertOne(t);
                            } catch (Exception e) {
                            }
                            try {
                                players.updateOne(eq("ref", p.getRef()), set("team", t.getTeam()));
                            } catch (Exception e) {
                            }
                        });
                Thread.sleep(4000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}

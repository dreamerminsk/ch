package sx.shirogane;

import com.mongodb.client.MongoCollection;
import org.jsoup.nodes.Document;
import sx.shirogane.utils.MongoUtils;
import sx.shirogane.utils.OkUtils;

import java.io.IOException;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class TeamUpdater {

    public static void main(String... args) {
        MongoCollection<Team> teams = MongoUtils.F_STATS.getCollection("teams", Team.class);
        MongoCollection<org.bson.Document> stadiums = MongoUtils.F_STATS.getCollection("stadiums");
        MongoCollection<org.bson.Document> coaches = MongoUtils.F_STATS.getCollection("coaches");
        teams.find(eq("xxx", null)).forEach((Consumer<? super Team>) p -> {
            //players.find().forEach((Consumer<? super Player>) p -> {
            System.out.println(p.getRef());
            String ref = "https://www.championat.com" + p.getRef();
            try {
                Document doc = OkUtils.getPage(ref);
                doc.select("div.tournament-header__title-name").stream().limit(1)
                        .forEach(el -> {
                            try {
                                teams.updateOne(eq("ref", p.getRef()), set("team", el.text().trim()));
                            } catch (Exception e) {
                            }
                        });
                doc.select("ul.tournament-header__facts[data-type='team'] > li:nth-child(" + (1) + ")")
                        .forEach(el -> {
                            try {
                                String[] parts = el.ownText().split(",");
                                teams.updateOne(eq("ref", p.getRef()), set("city", parts[0].trim()));
                                teams.updateOne(eq("ref", p.getRef()), set("nation", parts[1].trim()));
                            } catch (Exception e) {
                            }
                        });
                doc.select("ul.tournament-header__facts li a").stream()
                        .filter(el -> el.attr("href").contains("/stadiums/"))
                        .forEachOrdered(el -> {
                            try {
                                stadiums.insertOne(new org.bson.Document()
                                        .append("ref", el.attr("href"))
                                        .append("stadium", el.ownText().trim()));
                            } catch (Exception e) {
                            }
                            try {
                                teams.updateOne(eq("ref", p.getRef()), set("stadium", el.ownText().trim()));
                            } catch (Exception e) {
                            }
                        });
                doc.select("ul.tournament-header__facts li a").stream()
                        .filter(el -> el.attr("href").contains("/coaches/"))
                        .forEachOrdered(el -> {
                            try {
                                coaches.insertOne(new org.bson.Document()
                                        .append("ref", el.attr("href"))
                                        .append("name", el.ownText().trim()));
                            } catch (Exception e) {
                            }
                            try {
                                teams.updateOne(eq("ref", p.getRef()), set("manager", el.ownText().trim()));
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


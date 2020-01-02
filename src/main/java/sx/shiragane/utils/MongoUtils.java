package sx.shiragane.utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import sx.shiragane.imdb.model.Movie;
import sx.shiragane.imdb.model.Name;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoUtils {

    public static final MongoClient mongoClient = MongoClients.create(settings);
    public static final MongoDatabase F_STATS = mongoClient.getDatabase("fStats");
    public static final MongoDatabase IMDB = mongoClient.getDatabase("imdb");
    public static final MongoCollection<Movie> IMDB_TITLES = IMDB.getCollection("titles", Movie.class);
    public static final MongoCollection<Name> IMDB_NAMES = IMDB.getCollection("names", Name.class);
    private static final ConnectionString connString = new ConnectionString(
            "mongodb://172.105.92.75:27017/"
    );
    private static final CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    public static final MongoClientSettings settings = MongoClientSettings.builder()
            .codecRegistry(pojoCodecRegistry)
            .applyConnectionString(connString)
            .retryWrites(false)
            .build();


}
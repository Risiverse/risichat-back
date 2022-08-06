import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class AppDatabase {
    private final MongoCollection<Document> mongoCollection;

    public AppDatabase(String hostname, String database, String collection) {
        MongoClient mongoClient = MongoClients.create(hostname);
        this.mongoCollection = mongoClient
                .getDatabase(database)
                .getCollection(collection);
    }

    public void insertMessage(String stringifiedJsonMessage) {
        Document parsedBsonMessage = Document.parse(stringifiedJsonMessage);
        mongoCollection.insertOne(parsedBsonMessage);
    }
}

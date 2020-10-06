package utils.database;

import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import model.restriction.Restriction;
import services.entities.ProductList;
import services.entities.User;

public class MongoDBManager {
    private String serverURL = "mongodb+srv://<user>:<password>@cluster0.r8dm6.mongodb.net/<database>?retryWrites=true&w=majority";
    private com.mongodb.client.MongoClient client;
    protected Datastore datastore;
    private String database;

    private static MongoDBManager instance;
    private String user;

    public MongoDBManager() {
    }

    public static MongoDBManager getMongoDBManager() {
        if (instance == null) {
            instance = new MongoDBManager();
        }
        return instance;
    }

    public void setCredentialsAndDatabase(String username, String password, String database) {
        serverURL = serverURL.replace("<user>", username).replace("<password>",password).replace("<database>",database);
        this.database = database;
        this.user = username;
    }

    public String getUser() {
        return this.user;
    }

    public String getDatabase() {
        return this.database;
    }

    public void connect() {
        client = MongoClients.create(serverURL);
        datastore = Morphia.createDatastore(client, database);
        datastore.getMapper().map(User.class);
        datastore.getMapper().mapPackage("model");
        datastore.getMapper().mapPackageFromClass(ProductList.class);
        datastore.getMapper().mapPackageFromClass(Restriction.class);
        datastore.ensureIndexes();
    }
    public boolean isConnected() {
        return datastore != null;
    }

    public void map(Class... javaClasses) {
        for (Class javaClass : javaClasses)
            datastore.getMapper().map(javaClass);
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public void close() {
        client.close();
        datastore = null;
    }
}

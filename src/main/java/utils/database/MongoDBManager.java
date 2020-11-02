package utils.database;

import com.mongodb.*;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import model.preprocessing.workflow.WorkflowDTO;
import services.entities.Workflow;

import java.util.concurrent.TimeUnit;

public class MongoDBManager {
    private String serverURL = "mongodb+srv://<user>:<password>@cluster0.r8dm6.mongodb.net/<database>?retryWrites=true&w=majority";
    private com.mongodb.client.MongoClient client;
    protected Datastore datastore;
    private String database;

    private static MongoDBManager instance;
    private String user;
    private Morphia morphia;

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
        MongoClientSettings build = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(serverURL))
                .applyToSocketSettings(builder -> builder.connectTimeout(10000, TimeUnit.MILLISECONDS))
                .build();

        MongoClientURI mongoClientURI = new MongoClientURI(serverURL);
        morphia = new Morphia();
        morphia.mapPackageFromClass(Workflow.class);
        morphia.mapPackageFromClass(WorkflowDTO.class);
        datastore = morphia.createDatastore(new MongoClient(mongoClientURI), database);
        //client = MongoClients.create(build);
        /*datastore = Morphia.createDatastore(client, database);
        datastore.getMapper().map(User.class);
        datastore.getMapper().mapPackage("model");
        datastore.getMapper().mapPackageFromClass(ProductList.class);
        datastore.getMapper().mapPackageFromClass(Restriction.class);
        datastore.ensureIndexes();*/

    }
    public boolean isConnected() {
        return datastore != null;
    }

    public void map(Class... javaClasses) {
        /*for (Class javaClass : javaClasses)
            datastore.getMapper().map(javaClass);*/
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public void close() {
        //client.close();
        datastore = null;
    }
}

package utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.database.MongoDBManager;


import static org.assertj.core.api.Assertions.assertThat;

public class MongoDB_ {

    public static final String USER = "new-user_31";
    public static final String PASSWORD = "UNexjLVEJ3CIafOl";
    public static final String DATABASE = "SatelliteProducts";
    private MongoDBManager database;

    @Before
    public void init() {
        database = MongoDBManager.getMongoDBManager();
    }

    @Test
    public void set_credentials_mongodb() {
        database.setCredentialsAndDatabase(USER, PASSWORD, DATABASE);
        assertThat(database.getUser()).isEqualTo(USER);
        assertThat(database.getDatabase()).isEqualTo(DATABASE);
        assertThat(database.isConnected()).isFalse();
    }

    @Test
    public void connect_mongodb() {
        database.setCredentialsAndDatabase(USER, PASSWORD, DATABASE);
        database.connect();
        assertThat(database.isConnected()).isTrue();
    }

    @Test
    public void get_datastore_mongodb() {
        assertThat(database.getDatastore()).isNull();
        database.setCredentialsAndDatabase(USER, PASSWORD, DATABASE);
        database.connect();
        assertThat(database.getDatastore()).isNotNull();
    }

    @Test
    public void close_connection() {
        database.setCredentialsAndDatabase(USER, PASSWORD, DATABASE);
        database.connect();
        assertThat(database.getDatastore()).isNotNull();
        database.close();
        assertThat(database.getDatastore()).isNull();
    }

    @After
    public void reset() {
        database.close();
    }



}





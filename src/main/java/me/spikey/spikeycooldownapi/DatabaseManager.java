package me.spikey.spikeycooldownapi;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseManager {

    private static File databaseFile;
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void createPluginTable(Connection connection, String tableName) {

        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");

            statement = connection.createStatement();
//            String query = """
//                    CREATE TABLE IF NOT EXISTS %s (\
//                    id INTEGER, \
//                    uuid VARCHAR NOT NULL,\
//                    lastused TIMESTAMP NOT NULL\
//                    );
//                    """.formatted(tableName);

            String query = """
                    CREATE TABLE IF NOT EXISTS %s (\
                    id INTEGER, \
                    uuid VARCHAR NOT NULL,\
                    lastused text NOT NULL\
                    );
                    """.formatted(tableName);

            statement.executeUpdate(query);
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initDatabase(Plugin plugin) {

        File databaseFolder = new File(plugin.getDataFolder(), "cooldowns.db");
        if (!databaseFolder.exists()) {
            try {
                databaseFolder.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        databaseFile = databaseFolder;

        Statement statement = null;
        try (Connection connection = getConnection()){
            Class.forName("org.sqlite.JDBC");

            statement = connection.createStatement();
            String query = """
                    CREATE TABLE IF NOT EXISTS plugins (\
                    name VARCHAR\
                    );
                    """;

            statement.executeUpdate(query);
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

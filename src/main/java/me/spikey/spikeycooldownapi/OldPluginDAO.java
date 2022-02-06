package me.spikey.spikeycooldownapi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.spikey.spikeycooldownapi.utils.UUIDUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class OldPluginDAO {

    public static void addPlugin(Connection connection, String name) {

        try {
            Class.forName("org.sqlite.JDBC");

            String query = """
                    INSERT INTO plugins (name) \
                    VALUES\
                    (?);
                    """;

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, name);

            statement.execute();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getPlugins(Connection connection) {
        List<String> plugins = Lists.newArrayList();

        try {
            Class.forName("org.sqlite.JDBC");

            String query = """
                    SELECT * FROM plugins;
                    """;
            PreparedStatement statement = connection.prepareStatement(query);

            statement.execute();
            ResultSet resultSet = statement.getResultSet();

            while (resultSet.next()) {
                plugins.add(resultSet.getString(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return plugins;
    }

    public static HashMap<UUID, HashMap<Integer, Timestamp>> getPluginCooldowns(Connection connection, String pluginName) {
        HashMap<UUID, HashMap<Integer, Timestamp>> cooldowns = Maps.newHashMap();

        try {
            Class.forName("org.sqlite.JDBC");

            String query = """
                    SELECT * FROM %s;
                    """.formatted(pluginName);
            PreparedStatement statement = connection.prepareStatement(query);

            statement.execute();
            ResultSet resultSet = statement.getResultSet();

            while (resultSet.next()) {
                int id = (byte) resultSet.getInt("id");
                UUID uuid = UUIDUtils.build(resultSet.getString("uuid"));
                Timestamp time = resultSet.getTimestamp("lastused");
                cooldowns.putIfAbsent(uuid, Maps.newHashMap());
                cooldowns.get(uuid).put(id, time);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cooldowns;
    }

    public static void removeCooldown (Connection connection, String pluginName, int id, UUID uuid) {
        PreparedStatement statement = null;

        try {
            Class.forName("org.sqlite.JDBC");
            String query = """
                    DELETE FROM %s WHERE id=? AND uuid=?;
                    """.formatted(pluginName);
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, UUIDUtils.strip(uuid));
            statement.execute();

            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateCooldown(Connection connection, String pluginName, UUID uuid, int id, Timestamp timestamp) {
        PreparedStatement statement = null;

        try {
            removeCooldown(connection, pluginName, id, uuid);

            Class.forName("org.sqlite.JDBC");
            String query = """
                    INSERT INTO %s (uuid, id, lastused) \
                    VALUES\
                    (?, ?, ?);
                    """.formatted(pluginName);
            statement = connection.prepareStatement(query);

            statement.setString(1, UUIDUtils.strip(uuid));
            statement.setInt(2, id);
            statement.setTimestamp(3, timestamp);

            statement.execute();
            statement.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

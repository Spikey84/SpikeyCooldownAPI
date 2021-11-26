package me.spikey.spikeycooldownapi;

import me.spikey.spikeycooldownapi.utils.SchedulerUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.List;

public class Main extends JavaPlugin {

    public static List<String> plugins;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        SchedulerUtils.setPlugin(this);
        DatabaseManager.initDatabase(this);

        SchedulerUtils.runDatabase((connection -> {
            plugins = PluginDAO.getPlugins(connection);
        }));
    }

    @Override
    public void onDisable() {

    }
}

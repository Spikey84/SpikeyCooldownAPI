package me.spikey.spikeycooldownapi;

import me.spikey.spikeycooldownapi.utils.SchedulerUtils;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.List;

public class Main extends JavaPlugin {

    public static List<String> plugins;

    private static LuckPerms luckPerms;

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, 13808);
        saveDefaultConfig();
        SchedulerUtils.setPlugin(this);
        DatabaseManager.initDatabase(this);

        SchedulerUtils.runDatabase((connection -> {
            plugins = PluginDAO.getPlugins(connection);
        }));

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
    }

    @Override
    public void onDisable() {

    }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }
}

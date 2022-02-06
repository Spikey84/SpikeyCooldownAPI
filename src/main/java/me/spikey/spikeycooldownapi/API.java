package me.spikey.spikeycooldownapi;

import com.google.common.collect.Maps;
import me.spikey.spikeycooldownapi.utils.PermissionUtils;
import me.spikey.spikeycooldownapi.utils.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class API {
    private String name;
    private String permBase;
    private Map<Integer, String> permissions;

    private HashMap<UUID, HashMap<Integer, LocalDateTime>> cooldowns;

    public API(String name, String permissionBase) {
        addPlugin(name);
        this.name = name;
        this.permBase = permissionBase;
        this.permissions = Maps.newHashMap();

        SchedulerUtils.runDatabase((connection -> {
            cooldowns = PluginDAO.getPluginCooldowns(connection, name);
        }));
    }

    public void registerCooldown(int id, String name) {
        permissions.put(id, name);
    }


    private int getTimeInMin(UUID uuid, int id) {
        return PermissionUtils.getNumberedPermissionValueMin("cooldownapi.%s.%s".formatted(permBase, permissions.get(id)), uuid);
    }

    public void updateCooldown(UUID uuid, int id, LocalDateTime time) {
        cooldowns.putIfAbsent(uuid, Maps.newHashMap());
        cooldowns.get(uuid).put(id, time);

        SchedulerUtils.runDatabaseAsync((connection -> {
            PluginDAO.updateCooldown(connection, name, uuid, id, time);
        }));

    }

    public void updateCooldown(UUID uuid, int id) {
        cooldowns.putIfAbsent(uuid, Maps.newHashMap());
        cooldowns.get(uuid).put(id, LocalDateTime.now());

        SchedulerUtils.runDatabaseAsync((connection -> {
            PluginDAO.updateCooldown(connection, name, uuid, id, LocalDateTime.now());
        }));

    }

    public void updateCooldown(Player player, int id) {
        UUID uuid = player.getUniqueId();
        updateCooldown(uuid, id);
    }

    public boolean isOnCooldown(UUID uuid, int id) {
        cooldowns.putIfAbsent(uuid, Maps.newHashMap());
        if (!cooldowns.get(uuid).containsKey(id)) return false;

        LocalDateTime timeOfLastRunning = cooldowns.get(uuid).get(id);
        LocalDateTime currentTime = LocalDateTime.now();

        int cooldownTimeInMinutes = getTimeInMin(uuid, id);

        LocalDateTime timeOfCooldownExpire = timeOfLastRunning.plusMinutes(cooldownTimeInMinutes);

        return currentTime.isBefore(timeOfCooldownExpire);
    }

    public boolean isOnCooldown(Player player, int id) {
        UUID uuid = player.getUniqueId();
        return isOnCooldown(uuid, id);
    }

    public String getRemainingFormattedLong(UUID uuid, int id) {
        LocalDateTime timeOfLastRunning = cooldowns.get(uuid).get(id);
        LocalDateTime currentTime = LocalDateTime.now();

        int cooldownTimeInMinutes = getTimeInMin(uuid, id);

        LocalDateTime timeOfCooldownExpire = timeOfLastRunning.plusMinutes(cooldownTimeInMinutes);

        long years = ChronoUnit.YEARS.between(currentTime, timeOfCooldownExpire);
        long months = ChronoUnit.MONTHS.between(currentTime, timeOfCooldownExpire) % 12;
        long days = ChronoUnit.DAYS.between(currentTime, timeOfCooldownExpire) % 30;
        long hours = ChronoUnit.HOURS.between(currentTime, timeOfCooldownExpire) % 24;
        long minutes = ChronoUnit.MINUTES.between(currentTime, timeOfCooldownExpire) % 60;
        long seconds = ChronoUnit.SECONDS.between(currentTime, timeOfCooldownExpire) % 60;

        String formatted = "%s%s%s%s%s%s";

        String year = "";
        if (years != 0) year = years + " Years(s) ";
        String month = "";
        if (months != 0) month = months + " Months(s) ";
        String day = "";
        if (days != 0) day = days + " Day(s) ";
        String hour = "";
        if (hours != 0) hour = hours + " Hour(s) ";
        String minute = "";
        if (minutes != 0) minute = minutes + " Minute(s) ";
        String second = "";
        if (seconds != 0) second = seconds + " Seconds(s)";
        return formatted.formatted(year, month, day, hour, minute, second);

    }

//    public String getRemainingFormatted(UUID uuid, int id) {
//        long millis = getRemainingMillis(uuid, id);
//
//        long minutes = (long) Math.floor(millis/60000);
//        long seconds = (millis/1000) % 60;
//
//        if (seconds < 10) return String.format("%s:0%s", minutes, seconds);
//        return String.format("%s:%s", minutes, seconds);
//
//    }
//
//    public String getRemainingFormattedLong(UUID uuid, int id) {
//        long millis = getRemainingMillis(uuid, id);
//
//        long minutes = (long) Math.floor(millis/60000);
//        long seconds = (millis/1000) % 60;
//
//        return String.format("%s minutes and %s seconds", minutes, seconds);
//
//    }
//
//    public String getRemainingFormatted(Player player, int id) {
//        return getRemainingFormatted(player.getUniqueId(), id);
//    }
//
//    public long getRemainingMinutes(UUID uuid, int id) {
//        long millis = getRemainingMillis(uuid, id);
//
//        long minutes = (long) Math.ceil(millis/60000);
//
//        return minutes;
//    }
//
//    public long getRemainingMinutes(Player player, int id) {
//        UUID uuid = player.getUniqueId();
//        return getRemainingMinutes(uuid, id);
//    }
//
//    public long getRemainingSeconds(UUID uuid, int id) {
//        long millis = getRemainingMillis(uuid, id);
//
//        long seconds = (millis/1000);
//
//        return seconds;
//    }
//
//    public long getRemainingSeconds(Player player, int id) {
//        UUID uuid = player.getUniqueId();
//        return getRemainingSeconds(uuid, id);
//    }
//
//    private long getRemainingMillis(UUID uuid, int id) {
//        cooldowns.putIfAbsent(uuid, Maps.newHashMap());
//        if (!cooldowns.get(uuid).containsKey(id)) return -1;
//
//        LocalDateTime timeOfLastRunning = cooldowns.get(uuid).get(id);
//        LocalDateTime currentTime = LocalDateTime.now();
//
//
//        //int differenceMil = currentTime - timeOfLastRunning;
//
//        int differenceMil = 1;//////////////////////////////////////////////////////////////////////////////////
//        return (int) ((getTimeInMin(uuid, id)*60000) - (differenceMil));
//    }



    public static void addPlugin(String name) {
        if (Main.plugins.contains(name)) return;
        Main.plugins.add(name);
        SchedulerUtils.runDatabase((connection -> {
            PluginDAO.addPlugin(connection, name);
            DatabaseManager.createPluginTable(connection, name);
        }));
    }
}

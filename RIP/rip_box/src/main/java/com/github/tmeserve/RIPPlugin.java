package com.github.tmeserve;

import com.github.tmeserve.commands.Key;
import com.github.tmeserve.commands.Location;
import com.github.tmeserve.database.Database;
import com.github.tmeserve.events.RIPDeath;
import com.github.tmeserve.util.TimeManager;

import org.bukkit.plugin.java.JavaPlugin;


public class RIPPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private TimeManager timeManager;
    private Database db;

    @Override
    public void onEnable() {
        getLogger().info("RIP Box is loading");
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.timeManager = new TimeManager(this, configManager);
        this.db = new Database(this);
        this.db.getProfileManager().loadProfiles();
        getServer().getPluginManager().registerEvents(new RIPDeath(this), this);
        // String keyCMDStr = getConfig().getString("commands.key.prefix");
        // String locationCMDStr = getConfig().getString("commands.location.prefix");
        getCommand("deathkey").setExecutor(new Key(this));
        getCommand("deathlocation").setExecutor(new Location(this));
        
    }
    @Override
    public void onDisable() {
        getLogger().info("RIP Box is unloading");
    }

    public ConfigManager getConfigManager()
    { return this.configManager; }

    public TimeManager getTimeManager()
    { return this.timeManager; }

    public Database getDB()
    { return this.db; }
}
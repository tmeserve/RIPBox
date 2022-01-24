package com.github.tmeserve;

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

        getServer().getPluginManager().registerEvents(new RIPDeath(this), this);
        
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
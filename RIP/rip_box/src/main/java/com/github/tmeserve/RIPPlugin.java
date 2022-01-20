package com.github.tmeserve;

import org.bukkit.plugin.java.JavaPlugin;


public class RIPPlugin extends JavaPlugin {

    ConfigManager configManger;

    @Override
    public void onEnable() {
        getLogger().info("RIP Box is loading");
        saveDefaultConfig();
        configManger = new ConfigManager(this);
    }
    @Override
    public void onDisable() {
        getLogger().info("RIP Box is unloading");
    }
}
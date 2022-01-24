package com.github.tmeserve;

import java.io.File;
import java.io.IOException;

import com.github.tmeserve.commands.Location;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {

    private File configFile;
    private FileConfiguration config;

    private RIPPlugin plugin;

    public ConfigManager(RIPPlugin plugin)
    {
        this.plugin = plugin;
    }

    public FileConfiguration getConfig()
    {
        return this.config;
    }

    private void createConfig()
    {
        configFile = new File(plugin.getDataFolder(), "");

        if (!configFile.exists())
        {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("", false);
        }

        config = new YamlConfiguration();
        try
        { config.load(configFile); }
        catch (IOException | InvalidConfigurationException e)
        { e.printStackTrace(); }
    }

    // public FileConfiguration getDefaultConfig()
    // {
    //     return plugin.getConfig();
    // }
    
}

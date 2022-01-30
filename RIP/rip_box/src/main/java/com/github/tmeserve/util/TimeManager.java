package com.github.tmeserve.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.tmeserve.ConfigManager;
import com.github.tmeserve.RIPPlugin;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TimeManager {

    RIPPlugin plugin;

    HashMap<Player, Long> playerDeaths = new HashMap<Player, Long>();
    HashMap<Player, Long> broadcastMessage = new HashMap<Player, Long>();
    ConfigManager configManager;
    int taskID;
    BukkitTask playBukkitTask;


    public TimeManager(RIPPlugin plugin, ConfigManager configManager)
    {
        this.plugin = plugin;
        this.configManager = configManager;

        startTask();
    }

    public void broadcastDeathLocation(Player p)
    {
        String message = Util.chat(this.plugin.getConfig().getString("death.broadcast_message"));
        message = message.replaceAll("{player}", p.getDisplayName());
        message = message.replaceAll("{location}", "");
        this.plugin.getServer().broadcastMessage(message);
    }

    public void sendPlayerDeathLocation(Player p, Long time)
    {
        int t = time.intValue();
        String message = Util.chat(this.plugin.getConfig().getString("death.broadcast_message"));
        message = message.replaceAll("{time}", String.valueOf(t));
        message = message.replaceAll("{location}", "");
        p.sendMessage(message);
    }

    public void startPlayerDeathTime(Player player)
    {
        Long start = System.currentTimeMillis();
        playerDeaths.put(player, start);
        broadcastMessage.put(player, start);
    }

    public void startTask()
    {
        BukkitRunnable playerDeathRunnable = new BukkitRunnable() {
            @Override
            public void run()
            {
                for (Map.Entry<Player, Long> set : playerDeaths.entrySet())
                {
                    // Key is player data
                    // set.getKey();
                    // Value is Long
                    // set.getValue();

                    Long time = System.currentTimeMillis() - set.getValue();
                    long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(time);

                    // Need to change 60 and 300 to config values
                    if (timeSeconds >= 300)
                    {
                        broadcastDeathLocation(set.getKey());
                    }
                    else if ((timeSeconds % 60) == 0)
                    {
                        sendPlayerDeathLocation(set.getKey(), timeSeconds);
                    }
                }
            }
        };

        // 0L = No delay | 20L = happens every second
        playBukkitTask = playerDeathRunnable.runTaskTimer(plugin, 0L, 20L);
    }
    
}

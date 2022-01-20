package com.github.tmeserve.util;

import java.util.HashMap;

import com.github.tmeserve.RIPPlugin;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeManger {

    RIPPlugin plugin;

    HashMap<Player, Long> playerDeaths = new HashMap<Player, Long>();
    HashMap<Player, Long> broadcastMessage = new HashMap<Player, Long>();

    public TimeManger(RIPPlugin plugin)
    {
        this.plugin = plugin;
    }

    public void startPlayerDeathTime(Player player)
    {
        Long start = System.currentTimeMillis();
        playerDeaths.put(player, start);
        broadcastMessage.put(player, start);

        BukkitRunnable playerDeathTask = new BukkitRunnable() {
            @Override
            public void run()
            {
                
            }
        };
    }
    
}

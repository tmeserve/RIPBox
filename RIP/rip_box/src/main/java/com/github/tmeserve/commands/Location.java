package com.github.tmeserve.commands;

import java.util.ArrayList;

import com.github.tmeserve.RIPPlugin;
import com.github.tmeserve.database.DeathData;
import com.github.tmeserve.database.Profile;
import com.github.tmeserve.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Location implements CommandExecutor
{
    private RIPPlugin plugin;

    public Location(RIPPlugin plugin)
    {
        this.plugin = plugin;
    }

    // ripdeath.commands.deathlocation

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (sender instanceof Player && cmd.getName().equalsIgnoreCase("deathlocation"))
        {
            Player player = (Player) sender;
            
            if (!player.hasPermission("ripdeath.commands.deathlocation"))
            {
                player.sendMessage(Util.chat(this.plugin.getConfig().getString("death.permission_denied")));
                return true;
            }

            if (args.length == 1)
            {
                Profile profile = this.plugin.getDB().getProfileManager().getProfile(args[0]);

                ArrayList<DeathData> deathDatas = profile.getDeathDatas();
                String msg = this.plugin.getConfig().getString("death.location_message");
                msg = Util.chat(msg);

                msg = msg.replace("{player}", args[0]);

                for (DeathData death : deathDatas)
                {
                    org.bukkit.Location l = death.getDeathLocation();
                    double x = l.getX();
                    double y = l.getY();
                    double z = l.getZ();
                    if (msg.contains("{location}"))
                    {
                        String temp = String.valueOf(x) + ", " + String.valueOf(y) + ", " + String.valueOf(z);

                        player.sendMessage(msg.replace("{location}", temp));
                    }
                    else
                    {
                        String sendMessage = msg;
                        if (msg.contains("{x}"))
                            sendMessage = sendMessage.replace("{x}", String.valueOf(x));
                        if (msg.contains("{y}"))
                            sendMessage = sendMessage.replace("{y}", String.valueOf(y));
                        if (msg.contains("{z}"))
                            sendMessage = sendMessage.replace("{z}", String.valueOf(z));
                        if (msg.contains("{world}"))
                            sendMessage = sendMessage.replace("{world}", l.getWorld().getName());
                        
                        player.sendMessage(sendMessage);
                    }
                }
            }
        }
        else if (sender instanceof ConsoleCommandSender && cmd.getName().equalsIgnoreCase("deathlocation"))
        {
            String message = this.plugin.getConfig().getString("death.console_not_allowed_message");
            message = Util.chat(message);
            sender.sendMessage(message);
        }
        return true;
    }
    
}

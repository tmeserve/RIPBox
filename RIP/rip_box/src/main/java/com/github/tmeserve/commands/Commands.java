package com.github.tmeserve.commands;

import com.github.tmeserve.RIPPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor{

    private RIPPlugin plugin;

    public Commands(RIPPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (sender instanceof Player && cmd.getName().equalsIgnoreCase("location"))
        {
            return true;
        }
        return true;
    }

}

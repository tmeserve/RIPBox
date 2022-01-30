package com.github.tmeserve.commands;

import com.github.tmeserve.RIPPlugin;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class CommandLocation extends BukkitCommand
{
    private RIPPlugin plugin;

    public CommandLocation(RIPPlugin plugin, String name)
    {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args)
    {

        return true;
    }
}

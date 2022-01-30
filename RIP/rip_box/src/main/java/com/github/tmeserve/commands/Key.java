package com.github.tmeserve.commands;

import com.github.tmeserve.RIPPlugin;
import com.github.tmeserve.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Key implements CommandExecutor
{
    private RIPPlugin plugin;

    public Key(RIPPlugin plugin)
    {
        this.plugin = plugin;
    }

    private ItemStack createKey(int amount)
    {
        ItemStack item = new ItemStack(Material.valueOf(this.plugin.getConfig().getString("death.key_material")), amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Util.chat(this.plugin.getConfig().getString("death.key_name")));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (sender instanceof Player && cmd.getName().equalsIgnoreCase("deathkey"))
        {
            Player player = (Player) sender;

            if (!player.hasPermission("ripdeath.commands.deathkey"))
            {
                player.sendMessage(Util.chat(this.plugin.getConfig().getString("death.permission_denied")));
                return true;
            }
            
            if (args.length == 2)
            {
                Player receiver = Bukkit.getPlayer(args[0]);
                int amount;
                try
                {
                    amount = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException e)
                {
                    String incorrectUsage = this.plugin.getConfig().getString("death.incorrect_usage_message");
                    String deathKeyUsage = this.plugin.getConfig().getString("death.deathkey_usage");
                    incorrectUsage = incorrectUsage.replace("{usage}", deathKeyUsage);
                    incorrectUsage = Util.chat(incorrectUsage);
                    player.sendMessage(incorrectUsage);
                    return true;
                }

                ItemStack key = this.createKey(amount);
                receiver.getInventory().addItem(key);
            }
            else if (args.length == 1)
            {
                int amount;
                try
                {
                    amount = Integer.parseInt(args[0]);
                }
                catch (NumberFormatException e)
                {
                    String incorrectUsage = this.plugin.getConfig().getString("death.incorrect_usage_message");
                    String deathKeyUsage = this.plugin.getConfig().getString("death.deathkey_usage");
                    incorrectUsage = incorrectUsage.replace("{usage}", deathKeyUsage);
                    incorrectUsage = Util.chat(incorrectUsage);
                    player.sendMessage(incorrectUsage);
                    return true;
                }

                ItemStack key = this.createKey(amount);
                player.getInventory().addItem(key);
            }
            else
            {
                String incorrectUsage = this.plugin.getConfig().getString("death.incorrect_usage_message");
                String deathKeyUsage = this.plugin.getConfig().getString("death.deathkey_usage");
                incorrectUsage = incorrectUsage.replace("{usage}", deathKeyUsage);
                incorrectUsage = Util.chat(incorrectUsage);
                sender.sendMessage(incorrectUsage);
                return true;
            }
            
            return true;
        }
        else if (sender instanceof ConsoleCommandSender && cmd.getName().equalsIgnoreCase("deathkey"))
        {
            if (args.length != 2)
            {
                String incorrectUsage = this.plugin.getConfig().getString("death.incorrect_usage_message");
                String deathKeyUsage = this.plugin.getConfig().getString("death.deathkey_usage");
                incorrectUsage = incorrectUsage.replace("{usage}", deathKeyUsage);
                incorrectUsage = Util.chat(incorrectUsage);
                sender.sendMessage(incorrectUsage);
                return true;
            }
            Player receiver = Bukkit.getPlayer(args[0]);
            int amount;
            try
            {
                amount = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e)
            {
                String incorrectUsage = this.plugin.getConfig().getString("death.incorrect_usage_message");
                String deathKeyUsage = this.plugin.getConfig().getString("death.deathkey_usage");
                incorrectUsage = incorrectUsage.replace("{usage}", deathKeyUsage);
                incorrectUsage = Util.chat(incorrectUsage);
                sender.sendMessage(incorrectUsage);
                return true;
            }
            ItemStack key = this.createKey(amount);
            receiver.getInventory().addItem(key);
        }
        return true;
    }
}

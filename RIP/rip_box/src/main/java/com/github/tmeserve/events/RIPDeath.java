package com.github.tmeserve.events;

import java.util.UUID;

import com.github.tmeserve.RIPPlugin;
import com.github.tmeserve.database.Profile;
import com.github.tmeserve.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RIPDeath implements Listener
{
    private RIPPlugin plugin;

    public RIPDeath(RIPPlugin plugin)
    {
        this.plugin = plugin;
    }

    private Location getExtraChest(Location chestLocation)
    {
        Location dChestLocation;
        Block chest = chestLocation.getBlock();
        Block north = chest.getRelative(BlockFace.NORTH);
        Block south = chest.getRelative(BlockFace.SOUTH);
        Block west = chest.getRelative(BlockFace.WEST);
        Block east = chest.getRelative(BlockFace.EAST);
        World world = chestLocation.getWorld();

        if (north.getType() == Material.AIR)
            dChestLocation = north.getLocation();
        else if (south.getType() == Material.AIR)
            dChestLocation = south.getLocation();
        else if (west.getType() == Material.AIR)
            dChestLocation = west.getLocation();
        else if (east.getType() == Material.AIR)
            dChestLocation = east.getLocation();
        else
        {
            dChestLocation = north.getLocation();
            Block above1 = chest.getRelative(BlockFace.UP);
            Block above2 = chest.getRelative(BlockFace.UP);

            if (!(above1.getType() == Material.AIR))
                above1.setType(Material.AIR);
            if (!(above2.getType() == Material.AIR))
                above2.setType(Material.AIR);
        }

        return dChestLocation;
    }

    public void setBlockToMaterial(Block b, Material m)
    {
        b.setType(m);
        b.getState().setType(m);
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e)
    {
        Player player = e.getEntity();
        String playerName = player.getDisplayName();
        UUID uuid = player.getUniqueId();

        Location deathLocation = player.getLocation();
        String worldName = deathLocation.getWorld().getName();
        double x = deathLocation.getX();
        double y = deathLocation.getY();
        double z = deathLocation.getZ();

        this.plugin.getDB().getProfileManager().handleProfileCreation(uuid, playerName, worldName, x, y, z);

        Profile profile = this.plugin.getDB().getProfileManager().getProfile(uuid);
        profile.getData().setDeathLocation(deathLocation);
        profile.getData().save();

        deathLocation.getBlock().setType(Material.CHEST);

        Location doubleChestLocation = getExtraChest(deathLocation);

        Block chestBlock = deathLocation.getBlock();
        setBlockToMaterial(chestBlock, Material.CHEST);
        Block dChestBlock = doubleChestLocation.getBlock();

        Inventory playerInv = player.getInventory();

        
        
        Chest chest = (Chest) chestBlock.getState();
        // this.plugin.getLogger().info("Type: " + dChestBlock.getState().toString());
        Chest dChest;
        

        if (playerInv.getSize() > 27)
        {
            setBlockToMaterial(dChestBlock, Material.CHEST);
            dChest = (Chest) dChestBlock.getState();
        }
        
        
        Inventory chestInv = chest.getInventory();
        this.plugin.getLogger().info("Chest Size: " + String.valueOf(chestInv.getSize()));
        
        int index = 0;

        for (ItemStack item : playerInv.getContents())
        {
            //
            chestInv.setItem(index++, item);
        }

        chest.update();

        Block signBlock = deathLocation.getBlock().getRelative(BlockFace.NORTH);
        signBlock.setType(Material.WALL_SIGN);
        signBlock.getState().setType(Material.WALL_SIGN);

        Sign sign = (Sign) signBlock.getState();

        String signMessage = this.plugin.getConfig().getString("death.sign_message");
        signMessage = signMessage.replace("{player}", playerName);
        signMessage = Util.chat(signMessage);
        if (signMessage.length() > 15)
        {
            this.plugin.getLogger().info("testing 4");
            int remainder = signMessage.length() % 15;
            int length = signMessage.length();
            int line = 0;
            index = 0;
            this.plugin.getLogger().info("testing 3");

            while (line < 5)
            {
                this.plugin.getLogger().info("Length: {l}".replace("{l}", String.valueOf(length)));
                this.plugin.getLogger().info("Index: {i}".replace("{i}", String.valueOf(index)));
                this.plugin.getLogger().info("Remainder: {r}".replace("{r}", String.valueOf(remainder)));
                if ((length - index) == remainder)
                {
                    // sign.setLine(line, signMessage.substring(index, length - 1));
                    break;
                }
                else
                    sign.setLine(line, signMessage.substring(index, index+=15));
                line++;
            }
            sign.update();
            // }
        }
        else
            sign.setLine(0, signMessage);

        this.plugin.getLogger().info("Death completed");
    }

    @EventHandler
    public void onPlayerInteractionEvent(PlayerInteractEvent e)
    {
        // Look at DB for user
        Player player = e.getPlayer();

        Block block = e.getClickedBlock();

        if ((block instanceof Chest || block instanceof DoubleChest))
        {
            // Profile profile = profileManager;
        }
    }
    
}

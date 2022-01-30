package com.github.tmeserve.events;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.tmeserve.RIPPlugin;
import com.github.tmeserve.database.DeathData;
import com.github.tmeserve.database.Profile;
import com.github.tmeserve.util.Util;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.model.Filters;

import org.bukkit.ChatColor;
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

        // if (north.getType() == Material.AIR)
        //     dChestLocation = north.getLocation();
        // else if (south.getType() == Material.AIR)
        //     dChestLocation = south.getLocation();
        if (west.getType() == Material.AIR)
            dChestLocation = west.getLocation();
        else if (east.getType() == Material.AIR)
            dChestLocation = east.getLocation();
        else
        {
            dChestLocation = west.getLocation();
            Block above1 = chest.getRelative(BlockFace.UP);
            Block above2 = dChestLocation.getBlock().getRelative(BlockFace.UP);

            if (!(above1.getType() == Material.AIR))
                above1.setType(Material.AIR);
            if (!(above2.getType() == Material.AIR))
                above2.setType(Material.AIR);
        }

        return dChestLocation;
    }

    private Block getBlockRelative(Block b, Material m)
    {
        if (b == null)
            return null;

        if (b.getRelative(BlockFace.EAST).getType() == m)
            return b.getRelative(BlockFace.EAST);
        else if (b.getRelative(BlockFace.WEST).getType() == m)
            return b.getRelative(BlockFace.WEST);
        else if (b.getRelative(BlockFace.NORTH).getType() == m)
            return b.getRelative(BlockFace.NORTH);
        else if (b.getRelative(BlockFace.SOUTH).getType() == m)
            return b.getRelative(BlockFace.SOUTH);
        
        return null;
    }

    public void setBlockToMaterial(Block b, Material m)
    {
        b.setType(m);
        b.getState().setType(m);
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e)
    {
        Long deathTime = System.currentTimeMillis();

        Player player = e.getEntity();
        Inventory playerInv = player.getInventory();
        String playerName = player.getDisplayName();
        UUID uuid = player.getUniqueId();

        Location deathLocation = player.getLocation();
        String worldName = deathLocation.getWorld().getName();
        double x = deathLocation.getX();
        double y = deathLocation.getY();
        double z = deathLocation.getZ();

        this.plugin.getDB().getProfileManager().handleProfileCreation(uuid, playerName, worldName, x, y, z);

        Profile profile = this.plugin.getDB().getProfileManager().getProfile(uuid);
        profile.addDeathData(deathTime, deathLocation);
        // DeathData data = profile.getDeathData(deathTime);
        // profile.getData().setDeathLocation(deathLocation);
        // profile.getData().setDeathTime(deathTime);
        // profile.getData().save();

        Location doubleChestLocation = getExtraChest(deathLocation);

        Block chestBlock = deathLocation.getBlock();
        setBlockToMaterial(chestBlock, Material.CHEST);
        Block dChestBlock = null;
        
        Chest chest = (Chest) chestBlock.getState();
        Chest dChest;
        
        int itemAmount = 0;

        for (ItemStack item : playerInv.getContents())
        {
            if (item == null)
                continue;
            itemAmount++;
        }

        this.plugin.getLogger().info("Item Amount: " + String.valueOf(itemAmount));

        if (itemAmount <= 27)
        {
            setBlockToMaterial(chestBlock, Material.CHEST);
            this.plugin.getLogger().info("Less than");
        }
        else
        {
            dChestBlock = doubleChestLocation.getBlock();
            setBlockToMaterial(dChestBlock, Material.CHEST);
            dChest = (Chest) dChestBlock.getState();

            org.bukkit.block.data.type.Chest chestData = (org.bukkit.block.data.type.Chest) chestBlock.getBlockData();
            org.bukkit.block.data.type.Chest dChestData = (org.bukkit.block.data.type.Chest) dChestBlock.getBlockData();
            
            if (chestBlock.getRelative(BlockFace.WEST).getType() == Material.CHEST)
            {
                chestData.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
                dChestData.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
                this.plugin.getLogger().info("WEST");
            }
            else if (chestBlock.getRelative(BlockFace.EAST).getType() == Material.CHEST)
            {
                chestData.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
                dChestData.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);
                this.plugin.getLogger().info("EAST");
            }
            chest.setBlockData(chestData);
            dChest.setBlockData(dChestData);
            
            chest.update();
            dChest.update();
            
            this.plugin.getLogger().info("Elsed");
        }
        
        Inventory chestInv = chest.getInventory();

        for (ItemStack item : playerInv.getContents())
        {
            if (item == null)
                continue;
            chestInv.addItem(item);
        }
        playerInv.clear();

        Block signBlock = deathLocation.getBlock().getRelative(BlockFace.NORTH);
        if (signBlock.getType() == Material.CHEST)
            signBlock = dChestBlock.getRelative(BlockFace.NORTH);
        signBlock.setType(Material.OAK_WALL_SIGN);

        Sign sign = (Sign) signBlock.getState();

        String signMessage = this.plugin.getConfig().getString("death.sign_message");
        signMessage = signMessage.replace("{player}", playerName);
        signMessage = Util.chat(signMessage);
        if (signMessage.length() > 15)
        {
            int remainder = signMessage.length() % 15;
            int length = signMessage.length();
            int line = 0;
            int charsLeft = length;
            int index = 0;
            String lineStr = "";

            while (line < 4)
            {
                if (charsLeft <= 0)
                    break;
                else if (((length-1) - index) == 0)
                    break;
                else if (((length-1) - index) < 14)
                {
                    lineStr = signMessage.substring(index, length - 1);
                    sign.setLine(line, signMessage.substring(index, length - 1));
                    index = remainder;
                }
                else
                {
                    lineStr = signMessage.substring(index, length - 1);
                    sign.setLine(line, signMessage.substring(index, index+=14));
                }

                this.plugin.getLogger().info("lineStr: " + lineStr);
                    
                charsLeft -= (index + 1);
                line++;
            }
        }
        else
            sign.setLine(0, signMessage);
        sign.update();
        e.getDrops().clear();
        this.plugin.getLogger().info("Death completed");
    }

    @EventHandler
    public void onPlayerInteractionEvent(PlayerInteractEvent e)
    {
        this.plugin.getLogger().info("isEmpty: " + String.valueOf(this.plugin.getDB().getProfileManager().isEmpty()));
        ArrayList<ItemStack> items = new ArrayList<>();
        Player player = e.getPlayer();
        Long time = System.currentTimeMillis();

        Block chestBlock = e.getClickedBlock();
        Block doubleChestBlock = null;

        if (chestBlock == null)
            return;

        if (chestBlock.getType() != Material.CHEST)
            return;
        // Profile profile = profileManager;
        Chest chest = (Chest) chestBlock.getState();
        org.bukkit.block.data.type.Chest chestData = (org.bukkit.block.data.type.Chest) chest.getBlockData();
        if (chestData.getType() == org.bukkit.block.data.type.Chest.Type.LEFT)
            doubleChestBlock = chestBlock.getRelative(BlockFace.WEST);
        else if (chestData.getType() == org.bukkit.block.data.type.Chest.Type.RIGHT)
            doubleChestBlock = chestBlock.getRelative(BlockFace.EAST);
        else
            doubleChestBlock = getBlockRelative(chestBlock, Material.CHEST);

        Block signBlock = getBlockRelative(chestBlock, Material.OAK_WALL_SIGN);
        
        if (signBlock == null)
        {
            if (doubleChestBlock != null)
            {
                signBlock = getBlockRelative(doubleChestBlock, Material.OAK_WALL_SIGN);
                if (signBlock == null)
                    return;
            }
            else
                return;
        }
            
        
        if (signBlock.getType() != Material.OAK_WALL_SIGN)
            return;
        
        Sign sign = (Sign) signBlock.getState();
        String signMessage = "";

        for (String line : sign.getLines())
        {
            signMessage += line;
        }
        signMessage = ChatColor.stripColor(signMessage);

        String name = signMessage.split("'")[0];

        if (!name.equals(player.getDisplayName()))
        {
            // Look at DB for user
            Profile profile = this.plugin.getDB().getProfileManager().getProfile(name);
            this.plugin.getLogger().info("profileDatas: " + String.valueOf(profile.isEmpty()));
            DeathData data = profile.getDeathData(chestBlock.getLocation());
            this.plugin.getLogger().info("dchestblock: " + String.valueOf(data == null));
            if (data == null)
            {
                if (doubleChestBlock != null)
                {
                    data = profile.getDeathData(doubleChestBlock.getLocation());
                    this.plugin.getLogger().info("dchestblock: " + String.valueOf(data == null));
                    if (data == null)
                        return;
                }
                else
                    return;
            }
            Long deathTime = data.getDeathTime();
            Location deathLocation = data.getDeathLocation();
            Location chestLocation = chestBlock.getLocation();
            
            int publicTime = this.plugin.getConfig().getInt("broadcast_message_time");
            long differece = TimeUnit.MILLISECONDS.toSeconds(time - deathTime);
            int diff = (int) differece;
            ItemStack inUse = player.getItemInUse();
            Material m = Material.valueOf(this.plugin.getConfig().getString("death.key_material").toUpperCase());
            this.plugin.getLogger().info(inUse.getType().toString());
            if (!(inUse.getType() == m))
            {
                
                String message = Util.chat(this.plugin.getConfig().getString("death.key_needed_message"));
                player.sendMessage(message);
                e.setCancelled(true);
                return;
            }
            if (!(deathLocation.getX() == chestLocation.getX() && deathLocation.getY() == chestLocation.getX()
            && deathLocation.getZ() == chestLocation.getZ() && deathLocation.getWorld().getName().equals(chestLocation.getWorld().getName())
            && diff >= publicTime))
            {
                e.setCancelled(true);
                String message = Util.chat(this.plugin.getConfig().getString("death.access_denied"));
                message = message.replace("{player}", player.getDisplayName());
                message = message.replace("{time}", String.valueOf(diff));
                player.sendMessage(message);
                return;
            }

            player.getInventory().removeItem(new ItemStack(m, 1));
            String chestFoundMessage = Util.chat(this.plugin.getConfig().getString("death.chest_found_message"));
            if (chestFoundMessage.isEmpty())
                return;
            
            double x = deathLocation.getX();
            double y = deathLocation.getY();
            double z = deathLocation.getZ();
            
            if (chestFoundMessage.contains("{location}"))
            {
                String temp = String.valueOf(x) + ", " + String.valueOf(y) + ", " + String.valueOf(z);

                player.sendMessage(chestFoundMessage.replace("{location}", temp));
            }
            else
            {
                String sendMessage = chestFoundMessage;
                if (chestFoundMessage.contains("{x}"))
                    sendMessage = sendMessage.replace("{x}", String.valueOf(x));
                if (chestFoundMessage.contains("{y}"))
                    sendMessage = sendMessage.replace("{y}", String.valueOf(y));
                if (chestFoundMessage.contains("{z}"))
                    sendMessage = sendMessage.replace("{z}", String.valueOf(z));
                if (chestFoundMessage.contains("{world}"))
                    sendMessage = sendMessage.replace("{world}", deathLocation.getWorld().getName());
                
                player.sendMessage(sendMessage);
            }
            this.plugin.getServer().broadcastMessage(chestFoundMessage);
        }
        else
        {
            Profile profile = this.plugin.getDB().getProfileManager().getProfile(name);
            DeathData data = profile.getDeathData(chestBlock.getLocation());
            if (data == null)
            {
                data = profile.getDeathData(doubleChestBlock.getLocation());
                if (data == null)
                    return;
            }
            Location deathLocation = data.getDeathLocation();
            Location chestLocation = chestBlock.getLocation();
            String worldStr = deathLocation.getWorld().getName();
            double x = deathLocation.getX();
            double y = deathLocation.getY();
            double z = deathLocation.getZ();

            if ((deathLocation.getX() == chestLocation.getX() && deathLocation.getY() == chestLocation.getX()
            && deathLocation.getZ() == chestLocation.getZ() && deathLocation.getWorld().getName().equals(chestLocation.getWorld().getName())))
            {
                profile.removeDeathData(deathLocation);
                if (profile.isEmpty())
                {
                    Bson query = Filters.eq("uuid", player.getUniqueId().toString());
                    this.plugin.getDB().getProfileManager().removeProfile(name);
                    this.plugin.getDB().getServerCollection().deleteMany(query);
                }
                else
                {
                    BasicDBList critList = new BasicDBList();
                    BasicDBObject uuidDB = new BasicDBObject("uuid", new BasicDBObject("$eq", player.getUniqueId().toString()));
                    BasicDBObject worldNameDB = new BasicDBObject("world", new BasicDBObject("$eq", worldStr));
                    BasicDBObject xDB = new BasicDBObject("x", new BasicDBObject("$eq", String.valueOf(x)));
                    BasicDBObject yDB = new BasicDBObject("y", new BasicDBObject("$eq", String.valueOf(y)));
                    BasicDBObject zDB = new BasicDBObject("z", new BasicDBObject("$eq", String.valueOf(z)));
                    critList.add(uuidDB);
                    critList.add(worldNameDB);
                    critList.add(xDB);
                    critList.add(yDB);
                    critList.add(zDB);
                    Bson query = new BasicDBObject("$and", critList);
                    if (x == deathLocation.getX() && y == deathLocation.getY() && z == deathLocation.getZ()
                    && worldStr.equals(deathLocation.getWorld().getName()))
                    {
                        this.plugin.getLogger().info("deleting in DB");
                        this.plugin.getDB().getServerCollection().deleteOne(query);
                    }
                }
            }
        }
    }
    
}

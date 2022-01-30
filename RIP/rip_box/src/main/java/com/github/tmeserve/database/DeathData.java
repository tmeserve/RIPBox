package com.github.tmeserve.database;


import com.github.tmeserve.RIPPlugin;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class DeathData {

    private RIPPlugin plugin;
    private Database db;

    private java.util.UUID UUID;
    private String playerName;

    private Location deathLocation;
    private Long deathTime;

    public DeathData(RIPPlugin plugin, UUID uuid, String name)
    {
        this.plugin = plugin;
        
        this.db = this.plugin.getDB();
        this.UUID = uuid;
        this.playerName = name;
    }

    public void setDeathLocation(Location deathLocation)
    { this.deathLocation = deathLocation; }

    public Location getDeathLocation()
    { return this.deathLocation; }

    public void setDeathTime(Long deathTime)
    { this.deathTime = deathTime; }

    public Long getDeathTime()
    { return this.deathTime; }

    public void load()
    {
        Document document = db.getServerCollection().find(Filters.eq("uuid", UUID.toString())).first();
        if(document !=null)
        {
            String worldStr = document.getString("World");
            World world = this.plugin.getServer().getWorld(worldStr);
            double x = document.getDouble("X");
            double y = document.getDouble("Y");
            double z = document.getDouble("Z");
            deathLocation = new Location(world, x, y, z);
            deathTime = document.getLong("Death Time");
        }
    }

    public void save()
    {
        Document document = new Document();
        document.put("uuid", this.UUID.toString());
        document.put("name", this.playerName);
        document.put("time", this.deathTime);
        document.put("world", this.deathLocation.getWorld().getName());
        document.put("x", this.deathLocation.getX());
        document.put("y", this.deathLocation.getY());
        document.put("z", this.deathLocation.getZ());

        this.db.getServerCollection().insertOne(document);
    }
}

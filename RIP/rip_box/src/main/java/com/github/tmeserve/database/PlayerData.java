package com.github.tmeserve.database;


import com.github.tmeserve.RIPPlugin;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class PlayerData {

    private RIPPlugin plugin;
    private Database db = Database.instance;

    private java.util.UUID UUID;
    private String playerName;

    private Location deathLocation;
    private Long deathTime;

    public PlayerData(RIPPlugin plugin, UUID uuid, String name) {
        this.UUID = uuid;
        this.playerName = name;
        // this.deathLocation = location;
        // this.deathTime = deathTime;
    }

    public void setDeathLocation(Location deathLocation)
    { this.deathLocation = deathLocation; }

    public void setDeathTime(Long deathTime)
    { this.deathTime = deathTime; }

    public void resetPlayer() {
        
    }

    public void load() {
        Document document = db.getServerCollection().find(Filters.eq("uuid", UUID.toString())).first();
        if(document !=null) {
            String worldStr = document.getString("World");
            World world = this.plugin.getServer().getWorld(worldStr);
            double x = document.getDouble("X");
            double y = document.getDouble("Y");
            double z = document.getDouble("Z");
            deathLocation = new Location(world, x, y, z);
            deathTime = document.getLong("Death Time");
        }
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", this.UUID.toString());
        document.put("name", this.playerName);
        document.put("world", this.deathLocation.getWorld().getName());
        document.put("x", this.deathLocation.getX());
        document.put("y", this.deathLocation.getY());
        document.put("z", this.deathLocation.getZ());
        db.getServerCollection().replaceOne(Filters.eq("uuid", this.UUID.toString()), document, new UpdateOptions().upsert(true));
    }
}

package com.github.tmeserve.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.tmeserve.RIPPlugin;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import org.bson.Document;
import org.bukkit.Location;

public class Profile
{
    private RIPPlugin plugin;
    private Map<Location, DeathData> datas = new HashMap<>();
    private UUID UUID;
    private String playerName;

    public Profile(RIPPlugin plugin, UUID uuid, String name) {
        this.plugin = plugin;
        this.UUID = uuid;
        this.playerName = name;
        
    }

    public UUID getUUID()
    { return this.UUID; }

    public String getPlayerName()
    { return this.playerName; }

    public ArrayList<DeathData> getDeathDatas()
    {
        return new ArrayList<DeathData>(datas.values());
    }

    public DeathData getDeathData(Location l)
    {
        return datas.get(l);
    }

    public void addDeathData(Long time, Location l)
    {
        DeathData data = new DeathData(this.plugin, this.UUID, this.playerName);
        data.setDeathTime(time);
        data.setDeathLocation(l);
        data.save();
        datas.put(l, data);
    }

    public void removeDeathData(Location l)
    { this.datas.remove(l); }

    public boolean isEmpty()
    { return datas.size() == 0; }
}
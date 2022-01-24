package com.github.tmeserve.database;

import java.util.UUID;

import com.github.tmeserve.RIPPlugin;

import org.bukkit.Location;

public class Profile {


    private PlayerData data;
    private UUID UUID;
    private String playerName;

    public Profile(RIPPlugin plugin, UUID uuid, String name) {
        this.UUID = uuid;
        this.playerName = name;
        this.data = new PlayerData(plugin, uuid, name);
    }

    public UUID getUUID()
    { return this.UUID; }

    public String getPlayerName()
    { return this.playerName; }

    public PlayerData getData()
    { return this.data; }
}
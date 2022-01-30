package com.github.tmeserve.database;

import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.tmeserve.RIPPlugin;
import com.mongodb.Mongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class ProfileManager {

    private RIPPlugin plugin;
    private Database db;

    private Map<UUID, Profile> profiles = new HashMap<>();

    public ProfileManager(RIPPlugin plugin, Database db)
    {
        this.plugin = plugin;
        this.db = db;
    }

    public void handleProfileCreation(UUID uuid, String name, String worldName, double x, double y, double z)
    {
        if (!this.profiles.containsKey(uuid))
        {
            profiles.put(uuid, new Profile(this.plugin, uuid, name));
        }
    }

    public Profile getProfile(Object object)
    {
        if (object instanceof Player)
        {
            Player target = (Player) object;
            if (!this.profiles.containsKey(target.getUniqueId()))
            {
                return null;
            }
            return profiles.get(target.getUniqueId());
        }
        if (object instanceof UUID)
        {
            UUID uuid = (UUID) object;
            if (!this.profiles.containsKey(uuid))
            {
                return null;
            }
            return profiles.get(uuid);
        }
        if (object instanceof String)
        {
            return this.profiles.values().stream().filter(profile -> profile.getPlayerName().equalsIgnoreCase(object.toString())).findFirst().orElse(null);
        }
        return null;
    }

    public Map<UUID, Profile> getProfiles()
    { return this.profiles; }

    public void setProfiles(Map<UUID, Profile> profiles)
    { this.profiles = profiles; }

    public void removeProfile(Object object)
    { this.profiles.values().remove(this.getProfile(object)); }

    public void removeProfileIfEmpty(Object obj)
    {
        Profile profile = this.getProfile(obj);
        if (profile.isEmpty())
            this.profiles.values().remove(profile);
    }

    public void loadProfiles()
    {
        MongoCollection<Document> serverCollection = this.db.getServerCollection();

        MongoCursor<Document> cursor = serverCollection.find().iterator();

        try
        {
            while (cursor.hasNext())
            {
                Document doc = cursor.next();
                String uuidStr = doc.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);
                String name = doc.getString("name");
                Long time = doc.getLong("time");
                String worldName = doc.getString("world");
                double x = doc.getDouble("x");
                double y = doc.getDouble("y");
                double z = doc.getDouble("z");
                Location loc = new Location(this.plugin.getServer().getWorld(worldName), x, y, z);
                Profile profile = this.getProfile(uuid);
                if (profile == null)
                    profile = new Profile(this.plugin, uuid, name);
                profile.addDeathData(time, loc);
                this.profiles.put(uuid, profile);
            }
        } finally
        {
            cursor.close();
        }
    }

    public boolean isEmpty()
    {
        return profiles.size() == 0;
    }
}
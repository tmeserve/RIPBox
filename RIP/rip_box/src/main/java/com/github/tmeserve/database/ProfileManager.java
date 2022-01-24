package com.github.tmeserve.database;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.tmeserve.RIPPlugin;
import com.mongodb.Mongo;

public class ProfileManager {

    private RIPPlugin plugin;
    private Database db;

    private Map<UUID, Profile> profiles = new HashMap<>();

    public ProfileManager(Database db) {
        // super(db);
        this.db = db;
    }

    public void handleProfileCreation(UUID uuid, String name, String worldName, double x, double y, double z) {
        if (!this.profiles.containsKey(uuid)) {
            profiles.put(uuid, new Profile(this.plugin, uuid, name));
        }
    }

    public Profile getProfile(Object object) {
        if (object instanceof Player) {
            Player target = (Player) object;
            if (!this.profiles.containsKey(target.getUniqueId())) {
                return null;
            }
            return profiles.get(target.getUniqueId());
        }
        if (object instanceof UUID) {
            UUID uuid = (UUID) object;
            if (!this.profiles.containsKey(uuid)) {
                return null;
            }
            return profiles.get(uuid);
        }
        if (object instanceof String) {
            return this.profiles.values().stream().filter(profile -> profile.getPlayerName().equalsIgnoreCase(object.toString())).findFirst().orElse(null);
        }
        return null;
    }

    public Map<UUID, Profile> getProfiles() {
        return this.profiles;
    }

    public void setProfiles(Map<UUID, Profile> profiles) {
        this.profiles = profiles;
    }

    public void removeProfile(String uuid)
    {

        this.profiles.entrySet().removeIf(entry -> (entry.getKey().toString().equals(uuid)));
    }

    public void removeProfile(Profile profile)
    {
        this.profiles.values().remove(profile);
    }
}
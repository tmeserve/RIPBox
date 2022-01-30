package com.github.tmeserve.database;


import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.tmeserve.RIPPlugin;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;

public class Database {

    RIPPlugin plugin;

    public static Database instance;

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> serverCollection;
    private ProfileManager profileManager;

    public Database(RIPPlugin plugin)
    {
        this.plugin = plugin;
        System.setProperty("DEBUG.GO", "true");
        System.setProperty("DB.TRACE", "true");
        Logger mongoLobber = Logger.getLogger("org.mongodb.driver");
        mongoLobber.setLevel(Level.WARNING);
        connect();
        profileManager = new ProfileManager(plugin, this);
        instance = this;
    }

    public void connect()
    {
        FileConfiguration config = this.plugin.getConfig();
        String IP = config.getString("mongo.IP");
        this.plugin.getLogger().info("IP: {ip}".replace("{ip}", IP));
        int port = config.getInt("mongo.port");
        this.plugin.getLogger().info("Port: {port}".replace("{port}", String.valueOf(port)));
        String databaseName = config.getString("mongo.database");
        String collectionName = config.getString("mongo.collection");
        String username = config.getString("mongo.username");
        String password = config.getString("mongo.password");

        String uriStr = "mongodb://{user}:{pass}@{ip}:{port}/".replace("{user}", username).replace("{pass}", password).replace("{ip}", String.valueOf(IP)).replace("{port}", String.valueOf(port));
        MongoClientURI uri = new MongoClientURI(uriStr);
        
        client = new MongoClient(uri);
        database = client.getDatabase(databaseName);
        serverCollection = database.getCollection(collectionName);
        Bukkit.getLogger().info(ChatColor.GREEN + "Connected to MongoDB!");
    }

    public MongoCollection<Document> getServerCollection()
    { return serverCollection; }
    
    public ProfileManager getProfileManager()
    { return profileManager; }
}

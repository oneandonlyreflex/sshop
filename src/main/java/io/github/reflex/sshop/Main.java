package io.github.reflex.sshop;

import io.github.reflex.sshop.commands.ShopCommand;
import io.github.reflex.sshop.database.MongoDB;
import io.github.reflex.sshop.listener.InventoryClick;
import io.github.reflex.sshop.listener.InventoryOpen;
import io.github.reflex.sshop.manager.SpawnerManager;
import io.github.reflex.sshop.manager.UserManager;
import io.github.reflex.sshop.util.CommandMapProvider;
import io.github.reflex.sshop.util.Configs;
import io.github.reflex.sshop.util.SkullAPI;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import fr.mrmicky.fastinv.FastInvManager;

import java.util.Arrays;


public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    public static Economy economy;
    public Configs config;

    public SpawnerManager spawnerManager;
    public UserManager userManager;
    public MongoDB mongoDB;
    private boolean massimport;


    @Override
    public void onEnable() {
        instance = this;

        FastInvManager.register(this);
        config = new Configs("config.yml");
        config.saveDefaultConfig();

        massimport = config.getConfig().getBoolean("MongoDB.mass_import");

        spawnerManager = new SpawnerManager(this);
        userManager = new UserManager();

        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryOpen(), this);

        try {
            mongoDB = MongoDB.mongoRepository(this, config.getString("MongoDB.String"), config.getString("MongoDB.db_name"));
            if (massimport) {
                mongoDB.fetchAllUsers().whenComplete((users, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else {
                        users.forEach(userManager::register);
                        Bukkit.getConsoleSender().sendMessage("§aLoaded " + users.size() + " users.");
                    }
                });
            }
            SkullAPI.load();
            hookEconomy();
            CommandMapProvider.getCommandMap().registerAll("sshop", Arrays.asList(
                    new ShopCommand()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        mongoDB.pushMultipleUsersToDatabase(userManager.getUsers()).whenComplete((aBoolean, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                Bukkit.getConsoleSender().sendMessage("§aSSHOP users saved, shutting down");
                spawnerManager = null;
                userManager = null;
            }
        });
        //mongoDB.close();
    }

    private boolean hookEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return economy != null;
    }
}

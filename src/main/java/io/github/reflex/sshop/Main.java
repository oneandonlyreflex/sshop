package io.github.reflex.sshop;

import io.github.reflex.sshop.commands.ShopCommand;
import io.github.reflex.sshop.database.MongoDB;
import io.github.reflex.sshop.listener.InventoryClick;
import io.github.reflex.sshop.listener.InventoryOpen;
import io.github.reflex.sshop.listener.PlayerJoin;
import io.github.reflex.sshop.manager.SpawnerManager;
import io.github.reflex.sshop.manager.UserManager;
import io.github.reflex.sshop.util.*;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;


public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    public Configs config;

    public final SpawnerManager spawnerManager;
    public final UserManager userManager;
    public MongoDB mongoDB;
    public Economy economy;


    public Main() {
        this.spawnerManager = new SpawnerManager();
        this.userManager = new UserManager();
    }


    @Override
    public void onEnable() {
        instance = this;

        config = new Configs("config.yml");
        config.saveDefaultConfig();

        try {
            mongoDB = MongoDB.mongoRepository(this, config.getString("MongoDB.connection_String"), config.getString("MongoDB.db_name"));
            SkullAPI.load();
            hookEconomy();
            CommandMapProvider.getCommandMap().registerAll("sshop", Arrays.asList(
                    new ShopCommand(userManager, spawnerManager)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClick(economy, userManager, spawnerManager), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryOpen(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
    }

    @Override
    public void onDisable() {
        if (mongoDB.pushMultipleUsersToDatabaseSync(userManager.getUsers())) {
            Bukkit.getConsoleSender().sendMessage("§a[SSHOP] Users saved, shutting down");
            //CLEAR MANAGERS.
            mongoDB.close();
        } else {
            Bukkit.getConsoleSender().sendMessage("§c[SSHOP] There was an error saving the users.");
        }
    }

    private boolean hookEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return economy != null;
    }
}

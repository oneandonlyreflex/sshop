package io.github.reflex.sshop;

import io.github.reflex.sshop.database.MongoDB;
import io.github.reflex.sshop.listener.Test;
import io.github.reflex.sshop.manager.SpawnerManager;
import io.github.reflex.sshop.manager.UserManager;
import io.github.reflex.sshop.util.Configs;
import io.github.reflex.sshop.util.SkullAPI;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import fr.mrmicky.fastinv.FastInvManager;


public class Main extends JavaPlugin {

    public static Economy economy;
    public Configs config;
    public SpawnerManager spawnerManager;
    public UserManager userManager;
    public MongoDB mongoDB;
    @Getter
    private static Main instance;


    @Override
    public void onEnable() {
        instance = this;
        FastInvManager.register(this);
        config = new Configs("config.yml");
        config.saveDefaultConfig();

        spawnerManager = new SpawnerManager(this);
        userManager = new UserManager();
        Bukkit.getServer().getPluginManager().registerEvents(new Test(), this);

        try {
            mongoDB = MongoDB.mongoRepository(this, config.getString("MongoDB.String"), config.getString("MongoDB.db_name"));
            SkullAPI.load();
            hookEconomy();
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        mongoDB.close();
    }

    private boolean hookEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return economy != null;
    }
}

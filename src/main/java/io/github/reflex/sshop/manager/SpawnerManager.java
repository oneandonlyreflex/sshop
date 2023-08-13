package io.github.reflex.sshop.manager;

import io.github.reflex.sshop.Main;
import io.github.reflex.sshop.models.Spawner;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;

@Getter
public class SpawnerManager {

    private final Main main;

    public SpawnerManager(Main main) {
        this.main = main;
        loadSpawners();
    }

    protected final ArrayList<Spawner> spawnerArrayList = new ArrayList<>();

    public void loadSpawners() {
        for (String key : main.config.getConfig().getConfigurationSection("Spawners").getKeys(false)) {
            try {
                Spawner spawner = new Spawner(EntityType.valueOf(key), main.config.getConfig().getInt("Spawners." + key));
                spawnerArrayList.add(spawner);
            } catch(IllegalArgumentException e) {
                Bukkit.getConsoleSender().sendMessage("§a[SSHOP] §cConfig.yml Error: '§e"+key+"§c' Value in spawners config is not valid.");
            }
        }
        Bukkit.getConsoleSender().sendMessage("§a[SSHOP] §e"+spawnerArrayList.size()+"§a spawners loaded.");
    }

    public Spawner findSpawnerByType(EntityType entityType) {
        return spawnerArrayList.stream().filter(spawner -> spawner.getEntityType().equals(entityType)).findFirst().orElse(null);
    }
}

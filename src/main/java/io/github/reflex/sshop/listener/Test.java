package io.github.reflex.sshop.listener;

import io.github.reflex.sshop.Main;
import io.github.reflex.sshop.database.MongoDB;
import io.github.reflex.sshop.gui.SpawnerInventory;
import io.github.reflex.sshop.manager.SpawnerManager;
import io.github.reflex.sshop.manager.UserManager;
import io.github.reflex.sshop.models.Spawner;
import io.github.reflex.sshop.models.User;
import io.github.reflex.sshop.util.StringTranslator;
import lombok.var;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Test implements Listener {

    @EventHandler
    public void onjoin(PlayerJoinEvent e) {


    }

    @EventHandler
    public void onspeak(AsyncPlayerChatEvent e) {
      /*  Main.getInstance().userManager.throwSpawnerInHistory(e.getPlayer().getUniqueId(), EntityType.CREEPER);
        userManager.throwSpawnerInHistory(e.getPlayer().getUniqueId(), EntityType.CREEPER);
        userManager.throwSpawnerInHistory(e.getPlayer().getUniqueId(), EntityType.CREEPER);
        userManager.throwSpawnerInHistory(e.getPlayer().getUniqueId(), EntityType.CREEPER);
        userManager.throwSpawnerInHistory(e.getPlayer().getUniqueId(), EntityType.COW);
        userManager.throwSpawnerInHistory(e.getPlayer().getUniqueId(), EntityType.SPIDER);
        userManager.throwSpawnerInHistory(e.getPlayer().getUniqueId(), EntityType.SPIDER);
        userManager.throwSpawnerInHistory(e.getPlayer().getUniqueId(), EntityType.SPIDER);

     //   Main.getInstance().mongoDB.deleteDocument("id321");
        new SpawnerInventory(userManager.fetchUserWithId(e.getPlayer().getUniqueId()).getPlayerHistory(),e.getPlayer());
        ArrayList<String> players = new ArrayList<>();
        players.add("id123");
        players.add("id321");
       // Main.getInstance().mongoDB.deleteMany(players);
        Main.getInstance().mongoDB.updateUser(userManager.fetchUserWithId(e.getPlayer().getUniqueId()));*/
    }
}

package io.github.reflex.sshop.listener;

import io.github.reflex.sshop.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    /*
    * Loading user when he joins
    */

    @EventHandler
    public void loadPlayerOnJoin(PlayerJoinEvent playerJoinEvent) {
        Main.getInstance().mongoDB.fetchUser(playerJoinEvent.getPlayer().getUniqueId().toString()).thenAccept(user -> {
            if (user == null) {
                Main.getInstance().userManager.createUser(playerJoinEvent.getPlayer().getUniqueId());
            } else {
                Main.getInstance().userManager.register(user);
            }
        });
    }
}

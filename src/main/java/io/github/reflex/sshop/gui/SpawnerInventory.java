package io.github.reflex.sshop.gui;

import fr.mrmicky.fastinv.FastInv;
import io.github.reflex.sshop.Main;
import io.github.reflex.sshop.manager.UserManager;
import io.github.reflex.sshop.models.History;
import io.github.reflex.sshop.models.Spawner;
import io.github.reflex.sshop.models.User;
import io.github.reflex.sshop.util.InvAPI;
import io.github.reflex.sshop.util.Players;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpawnerInventory {//ALL FUCKED HAVE TO CHANGE THIS

    private final List<History> spawners;
    private final ArrayList<ItemStack> inventoryItems = new ArrayList<>();

    public SpawnerInventory(List<History> spawners, Player player) {
        this.spawners = spawners;

        spawners.stream()
                .sorted(Comparator.comparingDouble(History::getDateBought))
                .map(History::asItemStack)
                .forEach(inventoryItems::add);

        InvAPI mainInventory = new InvAPI.ScrollerBuilder().withName("§8Spawner History").withItems(inventoryItems).build();
        mainInventory.open(player);
    }
}

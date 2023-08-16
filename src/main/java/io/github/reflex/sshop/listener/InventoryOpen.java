package io.github.reflex.sshop.listener;

import io.github.reflex.sshop.gui.HistoryInventory;
import io.github.reflex.sshop.models.History;
import io.github.reflex.sshop.util.SkullAPI;
import io.github.reflex.sshop.util.Sort;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Comparator;

public class InventoryOpen implements Listener {

    private ItemStack sortItem(Sort sort) {
        ItemStack itemStack = SkullAPI.getByUrl("http://textures.minecraft.net/texture/1c6b9316ad145e6e63c7ef546a8cbcbfb28224293b3b6539d5725753a1cbdb26");
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("§aSort settings");

        switch (sort) {
            case DATE_REVERSED:
            case DATE:
                meta.setLore(Arrays.asList(
                        "",
                        "§a• Sort by Date (Right-click to change)",
                        "§7• Sort by Amount",
                        ""
                ));
                break;
            case AMOUNT:
            case AMOUNT_REVERSED:
                meta.setLore(Arrays.asList(
                        "",
                        "§7• Sort by Date",
                        "§a• Sort by Amount (Right-click to change)",
                        ""));
                break;
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @EventHandler
    public void onHistoryInventoryOpen(InventoryOpenEvent inventoryOpenEvent) {

        if (inventoryOpenEvent.getInventory().getName().equalsIgnoreCase("§8Spawner History")) {
            inventoryOpenEvent.getInventory().setItem(40, sortItem(HistoryInventory.getSORT()));
        }
    }
}

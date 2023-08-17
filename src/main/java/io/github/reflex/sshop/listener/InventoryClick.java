package io.github.reflex.sshop.listener;

import io.github.reflex.sshop.Main;
import io.github.reflex.sshop.gui.HistoryInventory;
import io.github.reflex.sshop.util.Players;
import io.github.reflex.sshop.util.Sort;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import io.github.reflex.sshop.util.InvAPI.ScrollerHolder;

public class InventoryClick implements Listener {


    @EventHandler
    public void onInventoryClickChangeSort(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getClickedInventory() == null) return;
        if (inventoryClickEvent.getInventory().getName().equalsIgnoreCase("§8Spawner History")) {
            ScrollerHolder inventoryHolder = (ScrollerHolder) inventoryClickEvent.getInventory().getHolder();
            Player player = (Player) inventoryClickEvent.getWhoClicked();
            if (inventoryClickEvent.getSlot() == 40) {
                if (inventoryClickEvent.isLeftClick()) {
                    if (inventoryHolder.getSortMethod().equals(Sort.DATE) || inventoryHolder.getSortMethod().equals(Sort.DATE_REVERSED)) {
                        inventoryHolder.setSortMethod(Sort.AMOUNT);
                    } else if (inventoryHolder.getSortMethod().equals(Sort.AMOUNT) || inventoryHolder.getSortMethod().equals(Sort.AMOUNT_REVERSED)) {
                        inventoryHolder.setSortMethod(Sort.DATE);
                    }
                } else if (inventoryClickEvent.isRightClick()) {
                    switch (inventoryHolder.getSortMethod()) {
                        case AMOUNT:
                            inventoryHolder.setSortMethod(Sort.AMOUNT_REVERSED);
                            break;
                        case AMOUNT_REVERSED:
                            inventoryHolder.setSortMethod(Sort.AMOUNT);
                            break;
                        case DATE:
                            inventoryHolder.setSortMethod(Sort.DATE_REVERSED);
                            break;
                        case DATE_REVERSED:
                            inventoryHolder.setSortMethod(Sort.DATE);
                            break;
                    }
                }
                inventoryHolder.replaceInventoryItems(Main.getInstance().userManager.getSortedList(Main.getInstance().userManager.fetchUserWithId(inventoryHolder.getOwner()).getPlayerHistory(), inventoryHolder.getSortMethod()));
                inventoryClickEvent.getWhoClicked().closeInventory();
                player.openInventory(inventoryHolder.getInventory());
            }
        }
    }

    @EventHandler
    public void buyItemSpawner(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getClickedInventory() == null) return;
        if (inventoryClickEvent.getClickedInventory().getName().equalsIgnoreCase("§8Spawner Shop")) {
            if (inventoryClickEvent.getCurrentItem() == null || inventoryClickEvent.getCurrentItem().getType().equals(Material.AIR))
                return;
            if (inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName().contains("Spawner")) {
                EntityType spawnerEntityType = EntityType.valueOf(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "").replace(" Spawner", "").toUpperCase());
                if (Main.getInstance().economy.getBalance((Player) inventoryClickEvent.getWhoClicked()) < Main.getInstance().spawnerManager.findSpawnerByType(spawnerEntityType).getCost()) {
                    inventoryClickEvent.getWhoClicked().sendMessage("§cYou dont have enough money for that.");
                    inventoryClickEvent.getWhoClicked().closeInventory();
                    return;
                } else {
                    ItemStack itemStack = new ItemStack(Material.MOB_SPAWNER, 1);
                    BlockStateMeta spawnerMeta = (BlockStateMeta) itemStack.getItemMeta();
                    spawnerMeta.setDisplayName(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName());
                    itemStack.setItemMeta(spawnerMeta);
                    inventoryClickEvent.getWhoClicked().getInventory().addItem(itemStack);
                    Main.getInstance().economy.withdrawPlayer((Player) inventoryClickEvent.getWhoClicked(), Main.getInstance().spawnerManager.findSpawnerByType(spawnerEntityType).getCost());
                    inventoryClickEvent.getWhoClicked().sendMessage("§aThank you for trusting us to buy your Spawners :D");
                    Main.getInstance().userManager.throwSpawnerInHistory(inventoryClickEvent.getWhoClicked().getUniqueId(), spawnerEntityType);
                }
            }
        }
    }
}

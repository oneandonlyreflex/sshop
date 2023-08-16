package io.github.reflex.sshop.listener;

import io.github.reflex.sshop.Main;
import io.github.reflex.sshop.gui.HistoryInventory;
import io.github.reflex.sshop.util.Sort;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class InventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {

        if (inventoryClickEvent.getInventory().getName().equalsIgnoreCase("§8Spawner History")) {
            if (inventoryClickEvent.getSlot() == 40) {
                if (inventoryClickEvent.isLeftClick()) {
                    if (HistoryInventory.getSORT().equals(Sort.DATE_REVERSED) || HistoryInventory.getSORT().equals(Sort.DATE)) {
                        HistoryInventory.updateSort(Sort.AMOUNT);
                        inventoryClickEvent.getWhoClicked().closeInventory();
                        new HistoryInventory(Main.getInstance().userManager.fetchUserWithId(inventoryClickEvent.getWhoClicked().getUniqueId()).getPlayerHistory() , (Player) inventoryClickEvent.getWhoClicked());
                    } else if (HistoryInventory.getSORT().equals(Sort.AMOUNT) || HistoryInventory.getSORT().equals(Sort.AMOUNT_REVERSED)){
                        HistoryInventory.updateSort(Sort.DATE_REVERSED);
                        inventoryClickEvent.getWhoClicked().closeInventory();
                        new HistoryInventory(Main.getInstance().userManager.fetchUserWithId(inventoryClickEvent.getWhoClicked().getUniqueId()).getPlayerHistory() , (Player) inventoryClickEvent.getWhoClicked());
                    }
                } else if (inventoryClickEvent.isRightClick()) {
                    switch (HistoryInventory.getSORT()) {
                        case DATE:
                            HistoryInventory.updateSort(Sort.DATE_REVERSED);
                            inventoryClickEvent.getWhoClicked().closeInventory();
                            new HistoryInventory(Main.getInstance().userManager.fetchUserWithId(inventoryClickEvent.getWhoClicked().getUniqueId()).getPlayerHistory(),
                                    (Player) inventoryClickEvent.getWhoClicked());
                            break;
                        case DATE_REVERSED:
                            HistoryInventory.updateSort(Sort.DATE);
                            inventoryClickEvent.getWhoClicked().closeInventory();
                            new HistoryInventory(Main.getInstance().userManager.fetchUserWithId(inventoryClickEvent.getWhoClicked().getUniqueId()).getPlayerHistory(),
                                    (Player) inventoryClickEvent.getWhoClicked());
                            break;
                        case AMOUNT:
                            HistoryInventory.updateSort(Sort.AMOUNT_REVERSED);
                            inventoryClickEvent.getWhoClicked().closeInventory();
                            new HistoryInventory(Main.getInstance().userManager.fetchUserWithId(inventoryClickEvent.getWhoClicked().getUniqueId()).getPlayerHistory(),
                                    (Player) inventoryClickEvent.getWhoClicked());
                            break;
                        case AMOUNT_REVERSED:
                            HistoryInventory.updateSort(Sort.AMOUNT);
                            inventoryClickEvent.getWhoClicked().closeInventory();
                            new HistoryInventory(Main.getInstance().userManager.fetchUserWithId(inventoryClickEvent.getWhoClicked().getUniqueId()).getPlayerHistory(),
                                    (Player) inventoryClickEvent.getWhoClicked());
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void buyItemSpawner(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getClickedInventory().getName().equalsIgnoreCase("§8Spawner Shop")) {
            if (inventoryClickEvent.getCurrentItem() == null || inventoryClickEvent.getCurrentItem().getType().equals(Material.AIR)) return;
            if (inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName().contains("Spawner")) {
                EntityType spawnerEntityType = EntityType.valueOf(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "").replace(" Spawner", "").toUpperCase());
                if (Main.economy.getBalance((Player)inventoryClickEvent.getWhoClicked()) < Main.getInstance().spawnerManager.findSpawnerByType(spawnerEntityType).getCost()) {
                    inventoryClickEvent.getWhoClicked().sendMessage("§cYou dont have enough money for that.");
                    inventoryClickEvent.getWhoClicked().closeInventory();
                    return;
                }else  {
                    ItemStack itemStack = new ItemStack(Material.MOB_SPAWNER,1);
                    BlockStateMeta spawnerMeta = (BlockStateMeta) itemStack.getItemMeta();
                    spawnerMeta.setDisplayName(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName());
                    itemStack.setItemMeta(spawnerMeta);
                    inventoryClickEvent.getWhoClicked().getInventory().addItem(itemStack);
                    Main.economy.withdrawPlayer((Player) inventoryClickEvent.getWhoClicked(),Main.getInstance().spawnerManager.findSpawnerByType(spawnerEntityType).getCost());
                    inventoryClickEvent.getWhoClicked().sendMessage("§aThank you for trusting us to buy your Spawners :D");
                    Main.getInstance().userManager.throwSpawnerInHistory(inventoryClickEvent.getWhoClicked().getUniqueId(), spawnerEntityType);
                }
            }
        }
    }
}

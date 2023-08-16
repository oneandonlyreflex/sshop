package io.github.reflex.sshop.gui;

import io.github.reflex.sshop.models.History;
import io.github.reflex.sshop.util.InvAPI;
import io.github.reflex.sshop.util.Sort;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HistoryInventory {

    @Getter
    private static Sort SORT;
    private final ArrayList<ItemStack> inventoryItems = new ArrayList<>();

    public HistoryInventory(@NotNull List<History> playerHistory, Player player) {
        InvAPI historyInventory;

        //The default is to sort the history based on date
        if (SORT == null) SORT = Sort.DATE_REVERSED;
        switch (SORT) {
            case DATE_REVERSED:
                playerHistory.stream()
                        .sorted(Comparator.comparingLong(History::getDateBought).reversed())
                        .map(History::asItemStack)
                        .forEach(inventoryItems::add);
                break;
            case DATE:
                playerHistory.stream()
                        .sorted(Comparator.comparingLong(History::getDateBought))
                        .map(History::asItemStack)
                        .forEach(inventoryItems::add);
                break;
            case AMOUNT:
                playerHistory.stream()
                        .sorted(Comparator.comparingLong(History::getAmountBought))
                        .map(History::asItemStack)
                        .forEach(inventoryItems::add);
                break;
            case AMOUNT_REVERSED:
                playerHistory.stream()
                        .sorted(Comparator.comparingLong(History::getAmountBought).reversed())
                        .map(History::asItemStack)
                        .forEach(inventoryItems::add);
                break;
        }

        if (inventoryItems.isEmpty()) {
            inventoryItems.add(new ItemStack(Material.WEB) {{
                ItemMeta meta = getItemMeta();
                meta.setDisplayName("§cNo history to display yet.");
                setItemMeta(meta);
            }});
            historyInventory = new InvAPI.ScrollerBuilder().withName("§8Spawner History").withItems(inventoryItems).withItemsSlots(22).build();
        } else {
            historyInventory = new InvAPI.ScrollerBuilder().withName("§8Spawner History").withItems(inventoryItems).build();
        }
        historyInventory.open(player);
    }

    public static void updateSort(Sort newSort) {
        SORT = newSort;
    }
}

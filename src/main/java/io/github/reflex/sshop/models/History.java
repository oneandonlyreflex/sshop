package io.github.reflex.sshop.models;

import io.github.reflex.sshop.Main;
import io.github.reflex.sshop.util.ItemBuilder;
import io.github.reflex.sshop.util.MobType;
import io.github.reflex.sshop.util.SkullAPI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy - HH:mm");

    private EntityType spawnerType;
    private long dateBought;
    private int amountBought;

    public ItemStack asItemStack() {
        return ItemBuilder.fromName(MobType.mobHeadTranslator(spawnerType))
                .name("§ax§e" + amountBought + " §d" + transformTypeName(spawnerType.toString()) + " Spawner")
                .lore(
                        "",
                        "§7Amount spent: §c" + gatherAmountSpent(spawnerType),
                        "§7Bought at: §c" + getFormattedBoughtTime(),
                        "") //fix method

                .build();
    }

    public void incrementAmountBought() {
        this.amountBought = getAmountBought() + 1;
    }

    public String getFormattedBoughtTime() {
        return DATE_FORMAT.format(new Date(dateBought));
    }
    private String transformTypeName(String s) {
        s = s.toLowerCase();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public double gatherAmountSpent(EntityType entityType) {
        return Main.getInstance().spawnerManager.findSpawnerByType(entityType).getCost() * amountBought;
    }
}

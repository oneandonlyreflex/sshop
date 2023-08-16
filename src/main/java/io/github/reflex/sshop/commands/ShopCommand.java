package io.github.reflex.sshop.commands;

import io.github.reflex.sshop.Main;
import io.github.reflex.sshop.gui.HistoryInventory;
import io.github.reflex.sshop.gui.SpawnerInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand extends Command {


    public ShopCommand() {
        super("sshop");
    }

    @Override
    public boolean execute(CommandSender s, String label, String[] args) {
        if (!(s instanceof Player)) {
            return false;
        }

        Player player = (Player) s;
        if (args.length == 0) {
            s.sendMessage("§cUsage: /sshop (buy/history)");
            return false;
        }
        if (args.length == 1) {
            switch (args[0]) {
                case "buy":
                    new SpawnerInventory(Main.getInstance().spawnerManager.getSpawnerList(), player);
                    break;
                case "history":
                    new HistoryInventory(Main.getInstance().userManager.fetchUserWithId(player.getUniqueId()).getPlayerHistory() , player);
                    break;
                default:
                    s.sendMessage("§cUsage: /sshop (buy/history)");
                    break;

            }
            return false;
        }
        return false;
    }
}

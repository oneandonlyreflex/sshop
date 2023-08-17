package io.github.reflex.sshop.commands;

import io.github.reflex.sshop.Main;
import io.github.reflex.sshop.gui.HistoryInventory;
import io.github.reflex.sshop.gui.SpawnerInventory;
import io.github.reflex.sshop.util.Players;
import io.github.reflex.sshop.util.Sort;
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
        if (args.length < 1) {
            s.sendMessage("§cUsage: /sshop (buy/history)");
            return false;
        }
        if (args.length == 1) {
            switch (args[0]) {
                case "buy":
                    new SpawnerInventory(Main.getInstance().spawnerManager.getSpawnerList(), player);
                    break;
                case "history":
                    new HistoryInventory(Main.getInstance().userManager
                            .getSortedList(Main.getInstance().userManager.fetchUserWithId(player.getUniqueId()).getPlayerHistory(), Sort.AMOUNT_REVERSED) , player, player.getUniqueId());
                    break;
                default:
                    s.sendMessage("§cUsage: /sshop (buy/history)");
                    break;

            }
            return false;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("history")) {
                if (Players.fetchPlayerUniqueId(args[1]) == null) {
                    s.sendMessage("§cThat player does not exist.");
                    return false;
                }
                if (Main.getInstance().userManager.fetchUserWithId(Players.fetchPlayerUniqueId(args[1])) == null) {
                    //Player is not in the cache lets search for him in the database
                    Main.getInstance().mongoDB.fetchUser(Players.fetchPlayerUniqueId(args[1]).toString()).thenAccept(user -> {
                        if (user == null) {
                            //User is not in database
                            s.sendMessage("§cThere are no avaliable data for that user's history");
                        } else {
                            //loading user to cache
                            s.sendMessage("§aOpening " + Players.getOfflinePlayer(user.getPlayerId()).getName() + "'s history");
                            Main.getInstance().userManager.register(user);
                            new HistoryInventory(Main.getInstance().userManager.getSortedList(user.getPlayerHistory(), Sort.AMOUNT_REVERSED), player, user.getPlayerId());
                        }
                    });
                    return false;
                }
            } else {
                s.sendMessage("§cUsage: /sshop history (player)");
            }
            return false;
        }
        return false;
    }
}

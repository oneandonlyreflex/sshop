package io.github.reflex.sshop.manager;


import io.github.reflex.sshop.Main;
import io.github.reflex.sshop.models.History;
import io.github.reflex.sshop.models.User;
import io.github.reflex.sshop.util.Players;
import lombok.Getter;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserManager {


    @Getter
    private  final List<User> users = new ArrayList<>();


    public String testes() {
        return "sdf";
    }
    public User fetchUserWithId(UUID playerId) {
        return users.stream().filter(user -> user.getPlayerId().equals(playerId)).findFirst().orElse(null);
    }
    public User fetchUserWithName(String playerName) {
        return users.stream().filter(user -> user.getPlayerId().equals(Players.fetchPlayerUniqueId(playerName))).findFirst().orElse(null);
    }

    public void createUser(UUID playerId) {
        if (fetchUserWithId(playerId) == null) {
            users.add(new User(playerId, new ArrayList<>()));
            System.out.println(testes());
        }
    }

    public History fetchHistoryByEntity(UUID playerId,EntityType entityType) {
        return fetchUserWithId(playerId).getPlayerHistory().stream().filter(history -> history.getSpawnerType().equals(entityType)).findFirst().orElse(null);
    }

    public void throwSpawnerInHistory(UUID playerId, EntityType type) {
        if (fetchHistoryByEntity(playerId, type) == null) {
            fetchUserWithId(playerId).getPlayerHistory().add(new History(type, System.currentTimeMillis(), 1));
        }else {
            fetchHistoryByEntity(playerId, type).incrementAmountBought();
        }
    }
}

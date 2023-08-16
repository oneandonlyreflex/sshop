package io.github.reflex.sshop.manager;

import io.github.reflex.sshop.models.History;
import io.github.reflex.sshop.models.User;
import io.github.reflex.sshop.util.Players;
import lombok.Getter;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    @Getter
    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    public User fetchUserWithId(UUID playerId) {
        return users.get(playerId);
    }

    public User fetchUserWithName(String playerName) {
        return users.get(Players.fetchPlayerUniqueId(playerName));
    }

    public void createUser(UUID playerId) { //Method creates a Non-existing user
        users.putIfAbsent(playerId, new User(playerId, new ArrayList<>()));
    }

    public History fetchHistoryByEntity(UUID playerId,EntityType entityType) {
        return users.get(playerId).getPlayerHistory().stream().filter(history -> history.getSpawnerType().equals(entityType)).findFirst().orElse(null);
    }

    public void register(User user) { //Method registers a user that is already on the database
        users.putIfAbsent(user.getPlayerId(), user);
    }

    public void throwSpawnerInHistory(UUID playerId, EntityType type) {
        if (fetchHistoryByEntity(playerId, type) == null) {
            fetchUserWithId(playerId).getPlayerHistory().add(new History(type, System.currentTimeMillis(), 1));
        }else {
            fetchHistoryByEntity(playerId, type).incrementAmountBought();
        }
    }
}

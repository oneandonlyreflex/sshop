package io.github.reflex.sshop.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import io.github.reflex.sshop.models.History;
import io.github.reflex.sshop.models.User;
import io.github.reflex.sshop.util.StringTranslator;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MongoDB {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private final Executor executor = Executors.newCachedThreadPool();
    private Plugin plugin;
    private final MongoCollection<Document> collection = database.getCollection("sshop");

    public MongoDB(Plugin plugin) {
        this.plugin = plugin;
    }

    public static MongoDB mongoRepository(Plugin plugin, String connectionString, String databaseName) throws Exception {
        try {
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .build();
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(databaseName);
            plugin.getLogger().info("Connected to MongoDB database '" + databaseName + "'.");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to connect to MongoDB: " + e.getMessage());
        }
        return new MongoDB(plugin);
    }

    public CompletableFuture<Boolean> pushUserToDatabase(User user) {
        Document filter = new Document("playerId", user.getPlayerId().toString());

        FindIterable<Document> documents = collection.find(filter);
        boolean userExists = documents.iterator().hasNext();

        if (!userExists) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                   return collection.insertOne(new Document().append("playerId", user.getPlayerId().toString())
                           .append("history", StringTranslator.historyTranslated(user.getPlayerHistory()))).wasAcknowledged();
                } catch (Exception e) {
                    return false;
                }
            }, executor);
        } else  {
            updateUser(user);
            return CompletableFuture.completedFuture(false);
        }
    }

    public CompletableFuture<Boolean> pushMultipleUsersToDatabase(List<User> userList) {
        List<CompletableFuture<Boolean>> userFutures = new ArrayList<>();

        for (User user : userList) {
            Document filter = new Document("playerId", user.getPlayerId().toString());
            FindIterable<Document> documents = collection.find(filter);
            boolean userExists = documents.iterator().hasNext();
            if (!userExists) {
                CompletableFuture<Boolean> userFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        return collection.insertOne(new Document()
                                        .append("playerId", user.getPlayerId().toString())
                                        .append("history", StringTranslator.historyTranslated(user.getPlayerHistory())))
                                .wasAcknowledged();
                    } catch (Exception e) {
                        return false;
                    }
                }, executor);
                userFutures.add(userFuture);
            } else {
                updateUser(user);
            }
        }

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0]));
        return combinedFuture.thenApply(result -> {
            // Check if all users were successfully added
            for (CompletableFuture<Boolean> userFuture : userFutures) {
                if (!userFuture.join()) {
                    return false;
                }
            }
            return true;
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return false;
        });
    }


    public CompletableFuture<Boolean> deleteMany(List<String> playerIds) {
        Document query = new Document("playerId", new Document("$in", playerIds));
        return CompletableFuture.supplyAsync(() -> {
            try {
                return collection.deleteMany(query).getDeletedCount() > 0;
            } catch (Exception e) {
                return false;
            }
        }, executor);
    }


    public CompletableFuture<Boolean> updateUser(User user) {
        Document filter = new Document("playerId", user.getPlayerId().toString());

        return CompletableFuture.supplyAsync(() -> {
            try {
                Document updateDoc = new Document("$set", new Document("history", StringTranslator.historyTranslated(user.getPlayerHistory())));
                UpdateResult updateResult = collection.updateOne(filter, updateDoc);
                return updateResult.getModifiedCount() > 0;
            } catch (Exception e) {
                return false;
            }
        }, executor);
    }

    public CompletableFuture<List<User>> fetchAllUsers() {
        FindIterable<Document> documents = collection.find();

        return CompletableFuture.supplyAsync(() -> {
            try {
                List<User> userList = new ArrayList<>();
                for (Document document : documents) {
                    User user = convertDocumentToUser(document);
                    userList.add(user);
                }
                return userList;
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch users", e);
            }
        }, executor);
    }

    public CompletableFuture<Boolean> deleteUser(User user) {
        Document filter = new Document("playerId", user.getPlayerId().toString());

        return CompletableFuture.supplyAsync(() -> {
            try {
                return collection.deleteOne(filter).getDeletedCount() > 0;
            } catch (Exception e) {
                return false;
            }
        }, executor);
    }


    private User convertDocumentToUser(Document document) {
        UUID playerId = UUID.fromString(document.getString("playerId"));
        List<History> playerHistory = StringTranslator.retrievedHistory(document.getString("history"));
        return new User(playerId, playerHistory);
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}

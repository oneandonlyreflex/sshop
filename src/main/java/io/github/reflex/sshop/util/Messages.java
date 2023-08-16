package io.github.reflex.sshop.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

public enum Messages {

    FIRST("first"),


    ;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String[] message;
    private String patch;

    Messages(String patch) {
        this.patch = patch;
    }

    public String[] getMessage(String... replacements) {
        Validate.isTrue((replacements.length % 2) == 0, "Invalid replacement!");
        String[] msg = message.clone();

        if (replacements.length == 0) {
            return msg;
        }

        for (int index = 0; index < this.message.length; index++) {
            for (int i = 0; i <= (replacements.length / 2) - 1; i++) {
                msg[index] = msg[index].replace(replacements[i * 2], replacements[(i * 2) + 1]);
            }
        }

        return msg;
    }

    public static void setup(File file) {
        Validate.notNull(file, "You cannot setup the messages with an invalid file!");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        for (Messages value : values()) {
            String patch = value.patch;

            if (!configuration.contains(patch)) {
                continue;
            }

            String[] message = configuration.isList(patch) ? configuration.getStringList(patch).toArray(new String[0]) : new String[] { configuration.getString(patch) };
            for (int index = 0; index < message.length; index++) {
                if (message[index] == null) {
                    continue;
                }

                message[index] = message[index].replace('&', '§');
            }

            value.setMessage(message);
        }
    }

    public Messages withReplacements(String... placeholdersAndReplacements) {
        Map<String, String> replacements = new HashMap<>();
        if (placeholdersAndReplacements.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
        for (int i = 0; i < placeholdersAndReplacements.length; i += 2) {
            replacements.put(placeholdersAndReplacements[i], placeholdersAndReplacements[i + 1]);
        }

        String[] modifiedMessage = new String[message.length];
        for (int index = 0; index < message.length; index++) {
            String modifiedMsg = message[index];
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                modifiedMsg = modifiedMsg.replace(entry.getKey(), entry.getValue());
            }
            modifiedMessage[index] = modifiedMsg;
        }

        setMessage(modifiedMessage);
        return this;
    }
}


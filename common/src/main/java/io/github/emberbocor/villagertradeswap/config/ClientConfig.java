package io.github.emberbocor.villagertradeswap.config;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;

public final class ClientConfig {
    private static final String FILE_NAME = VillagerTradeSwap.MODID + "-client.json";
    private static final String SHOW_REROLL_BUTTONS = "showRerollButtons";

    private static boolean loaded;
    private static boolean showRerollButtons = true;

    private ClientConfig() {
    }

    public static boolean showRerollButtons() {
        if (!loaded) {
            load();
        }
        return showRerollButtons;
    }

    public static void setShowRerollButtons(boolean show) {
        showRerollButtons = show;
        loaded = true;
        save(JsonConfigFile.resolve(FILE_NAME));
    }

    private static void load() {
        loaded = true;
        Path file = JsonConfigFile.resolve(FILE_NAME);
        if (Files.notExists(file)) {
            save(file);
            return;
        }
        JsonConfigFile.read(file).ifPresent(json -> {
            JsonElement value = json.get(SHOW_REROLL_BUTTONS);
            if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
                showRerollButtons = value.getAsBoolean();
            } else {
                VillagerTradeSwap.LOGGER.warn("Invalid {} in {} (expected true or false), using true", SHOW_REROLL_BUTTONS, file);
            }
        });
    }

    private static void save(Path file) {
        JsonObject json = new JsonObject();
        json.addProperty(SHOW_REROLL_BUTTONS, showRerollButtons);
        JsonConfigFile.write(file, json);
    }
}

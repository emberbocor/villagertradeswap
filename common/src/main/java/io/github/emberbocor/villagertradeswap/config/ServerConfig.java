package io.github.emberbocor.villagertradeswap.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;

public final class ServerConfig {
    private static final String FILE_NAME = VillagerTradeSwap.MODID + ".json";
    private static final String REROLL_COST = "rerollCost";
    private static final int DEFAULT_REROLL_COST = 1;
    private static final int MAX_REROLL_COST = 64;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static int rerollCost = DEFAULT_REROLL_COST;

    private ServerConfig() {
    }

    public static int rerollCost() {
        return rerollCost;
    }

    public static void load(Path configDirectory) {
        Path file = configDirectory.resolve(FILE_NAME);
        if (Files.notExists(file)) {
            rerollCost = DEFAULT_REROLL_COST;
            writeDefaults(file);
        } else {
            rerollCost = readRerollCost(file);
        }
    }

    private static int readRerollCost(Path file) {
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonElement value = JsonParser.parseReader(reader).getAsJsonObject().get(REROLL_COST);
            if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
                double cost = value.getAsDouble();
                if (cost == Math.rint(cost) && cost >= 0 && cost <= MAX_REROLL_COST) {
                    return (int) cost;
                }
            }
            VillagerTradeSwap.LOGGER.warn("Invalid {} in {} (expected a whole number from 0 to {}), using {}",
                    REROLL_COST, file, MAX_REROLL_COST, DEFAULT_REROLL_COST);
        } catch (IOException | RuntimeException e) {
            VillagerTradeSwap.LOGGER.warn("Could not read {}, using {} {}: {}", file, REROLL_COST, DEFAULT_REROLL_COST, e.getMessage());
        }
        return DEFAULT_REROLL_COST;
    }

    private static void writeDefaults(Path file) {
        JsonObject json = new JsonObject();
        json.addProperty(REROLL_COST, DEFAULT_REROLL_COST);
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(json, writer);
            }
        } catch (IOException e) {
            VillagerTradeSwap.LOGGER.warn("Could not create {}: {}", file, e.getMessage());
        }
    }
}

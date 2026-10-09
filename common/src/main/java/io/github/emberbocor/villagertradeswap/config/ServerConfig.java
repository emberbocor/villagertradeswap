package io.github.emberbocor.villagertradeswap.config;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;

public final class ServerConfig {
    private static final String FILE_NAME = VillagerTradeSwap.MODID + ".json";
    private static final String REROLL_COST = "rerollCost";
    private static final int DEFAULT_REROLL_COST = 1;
    private static final int MAX_REROLL_COST = 64;

    private static int rerollCost = DEFAULT_REROLL_COST;

    private ServerConfig() {
    }

    public static int rerollCost() {
        return rerollCost;
    }

    public static void load() {
        Path file = JsonConfigFile.resolve(FILE_NAME);
        if (Files.notExists(file)) {
            rerollCost = DEFAULT_REROLL_COST;
            JsonObject json = new JsonObject();
            json.addProperty(REROLL_COST, DEFAULT_REROLL_COST);
            JsonConfigFile.write(file, json);
        } else {
            rerollCost = JsonConfigFile.read(file).map(json -> readRerollCost(file, json)).orElse(DEFAULT_REROLL_COST);
        }
    }

    private static int readRerollCost(Path file, JsonObject json) {
        JsonElement value = json.get(REROLL_COST);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            double cost = value.getAsDouble();
            if (cost == Math.rint(cost) && cost >= 0 && cost <= MAX_REROLL_COST) {
                return (int) cost;
            }
        }
        VillagerTradeSwap.LOGGER.warn("Invalid {} in {} (expected a whole number from 0 to {}), using {}",
                REROLL_COST, file, MAX_REROLL_COST, DEFAULT_REROLL_COST);
        return DEFAULT_REROLL_COST;
    }
}

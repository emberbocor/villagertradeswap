package io.github.emberbocor.villagertradeswap.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.OptionalInt;

import com.google.gson.JsonElement;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;

final class LegacyServerConfig {
    static final String FILE_NAME = VillagerTradeSwap.MODID + ".json";

    private LegacyServerConfig() {
    }

    static OptionalInt migrateRerollCost(Path legacyFile, Path file) {
        if (Files.notExists(legacyFile)) {
            return OptionalInt.empty();
        }
        OptionalInt cost = JsonConfigFile.read(legacyFile)
                .map(json -> json.get(ServerConfigFile.REROLL_COST))
                .map(LegacyServerConfig::wholeCost)
                .orElse(OptionalInt.empty());
        if (cost.isPresent()) {
            VillagerTradeSwap.LOGGER.info("Took {} = {} from {}. That file is no longer used and can be deleted; the settings are now in {}",
                    ServerConfigFile.REROLL_COST, cost.getAsInt(), legacyFile, file);
        } else {
            VillagerTradeSwap.LOGGER.warn("{} has no valid {} (a whole number from 0 to {}), using the default. That file is no longer used and can be deleted; the settings are now in {}",
                    legacyFile, ServerConfigFile.REROLL_COST, ServerConfigReader.MAX_REROLL_COST, file);
        }
        return cost;
    }

    static void reportUnused(Path legacyFile, Path file) {
        if (Files.exists(legacyFile)) {
            VillagerTradeSwap.LOGGER.info("{} is no longer used and can be deleted; the settings are in {}", legacyFile, file);
        }
    }

    private static OptionalInt wholeCost(JsonElement value) {
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            double cost = value.getAsDouble();
            if (cost == Math.rint(cost) && cost >= 0 && cost <= ServerConfigReader.MAX_REROLL_COST) {
                return OptionalInt.of((int) cost);
            }
        }
        return OptionalInt.empty();
    }
}

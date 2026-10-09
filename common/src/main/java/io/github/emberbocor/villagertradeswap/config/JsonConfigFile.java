package io.github.emberbocor.villagertradeswap.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import io.github.emberbocor.villagertradeswap.platform.Services;

final class JsonConfigFile {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonConfigFile() {
    }

    static Path resolve(String fileName) {
        return Services.PLATFORM.getConfigDirectory().resolve(fileName);
    }

    static Optional<JsonObject> read(Path file) {
        try (Reader reader = Files.newBufferedReader(file)) {
            return Optional.of(JsonParser.parseReader(reader).getAsJsonObject());
        } catch (IOException | RuntimeException e) {
            VillagerTradeSwap.LOGGER.warn("Could not read {}, using defaults: {}", file, e.getMessage());
            return Optional.empty();
        }
    }

    static void write(Path file, JsonObject json) {
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(json, writer);
            }
        } catch (IOException e) {
            VillagerTradeSwap.LOGGER.warn("Could not write {}: {}", file, e.getMessage());
        }
    }
}

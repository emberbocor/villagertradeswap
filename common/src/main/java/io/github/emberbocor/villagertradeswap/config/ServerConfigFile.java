package io.github.emberbocor.villagertradeswap.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;

final class ServerConfigFile {
    static final String FILE_NAME = VillagerTradeSwap.MODID + "-server.toml";
    static final String REROLL_COST = "rerollCost";
    static final String REROLL_CURRENCY = "rerollCurrency";
    static final String ALLOW_SAME_TRADE_IN_LEVEL = "allowSameTradeInLevel";

    private static final String HEADER = """
            # Villager Trade Swap server config.
            # Created on the first server start. Your values and comments are never changed; options missing from this
            # file are added at the end with their default value. Changes take effect after a server restart (in
            # singleplayer: when the world is opened again).
            """;
    private static final List<Option> OPTIONS = List.of(
            new Option(REROLL_COST, """
                    # Price of one trade swap, paid with the rerollCurrency item. Whole number from 0 to 64; 0 makes
                    # swapping free. Players in Creative mode never pay, and nothing is taken when a swap fails.
                    """, "1"),
            new Option(REROLL_CURRENCY, """
                    # Item that pays for a trade swap, as an item id such as "minecraft:emerald" or "minecraft:diamond".
                    # Items from other mods work too; item tags (#...) do not. Every item of this type counts, whatever
                    # its name, enchantments or other data. It is taken from the main inventory and the hotbar, never
                    # from the trade's payment slots.
                    """, "\"minecraft:emerald\""),
            new Option(ALLOW_SAME_TRADE_IN_LEVEL, """
                    # A swap never gives a trade identical to the one being swapped or to any other trade of the same
                    # villager. Identical means the same items bought and sold with the same enchantments, potion or
                    # map destination; amounts and prices do not matter.
                    # true: a swap may give a trade of the same kind (the same items bought and sold) as another trade
                    # of that level, as long as it is not identical, for example a second enchanted book with another
                    # enchantment or a second explorer map to another structure.
                    # false: a level never holds two trades of the same kind.
                    """, "true"));

    private ServerConfigFile() {
    }

    static Optional<UnmodifiableConfig> load(Path file, Path legacyFile) {
        if (!Files.exists(file)) {
            String text = defaultText(LegacyServerConfig.migrateRerollCost(legacyFile, file));
            if (write(file, text)) {
                VillagerTradeSwap.LOGGER.info("Created {}", file);
            }
            return parse(file, text);
        }
        LegacyServerConfig.reportUnused(legacyFile, file);

        String text;
        try {
            text = Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            VillagerTradeSwap.LOGGER.warn("Could not read {}, using the default settings: {}", file, e.getMessage());
            return Optional.empty();
        }
        Optional<UnmodifiableConfig> config = parse(file, text.startsWith("﻿") ? text.substring(1) : text);
        config.ifPresent(values -> addMissingOptions(file, text, values));
        return config;
    }

    private static String defaultText(OptionalInt rerollCost) {
        StringBuilder text = new StringBuilder(HEADER);
        for (Option option : OPTIONS) {
            String value = option.key().equals(REROLL_COST) && rerollCost.isPresent() ? Integer.toString(rerollCost.getAsInt()) : option.defaultValue();
            text.append('\n').append(option.text(value));
        }
        return text.toString();
    }

    private static Optional<UnmodifiableConfig> parse(Path file, String text) {
        try {
            return Optional.of(TomlFormat.instance().createParser().parse(text));
        } catch (RuntimeException e) {
            VillagerTradeSwap.LOGGER.warn("Could not parse {}, using the default settings and leaving the file unchanged: {}", file, e.getMessage());
            return Optional.empty();
        }
    }

    private static void addMissingOptions(Path file, String text, UnmodifiableConfig config) {
        List<Option> missing = OPTIONS.stream().filter(option -> !config.contains(option.key())).toList();
        if (missing.isEmpty()) {
            return;
        }
        List<String> keys = missing.stream().map(Option::key).toList();
        if (config.entrySet().stream().anyMatch(entry -> entry.getValue() instanceof UnmodifiableConfig)) {
            VillagerTradeSwap.LOGGER.warn("Could not add the missing options {} to {}: the file contains a [table], so they would end up inside it; using their default values",
                    keys, file);
            return;
        }
        String newline = text.contains("\r\n") ? "\r\n" : "\n";
        StringBuilder addition = new StringBuilder();
        if (!text.isEmpty() && !text.endsWith("\n")) {
            addition.append(newline);
        }
        for (Option option : missing) {
            addition.append(newline).append(option.text(option.defaultValue()).replace("\n", newline));
        }
        try {
            Files.writeString(file, addition, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            VillagerTradeSwap.LOGGER.info("Added the missing options {} to {} with their default values", keys, file);
        } catch (IOException e) {
            VillagerTradeSwap.LOGGER.warn("Could not add the missing options {} to {}, using their default values: {}", keys, file, e.getMessage());
        }
    }

    private static boolean write(Path file, String text) {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, text, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            VillagerTradeSwap.LOGGER.warn("Could not write {}: {}", file, e.getMessage());
            return false;
        }
    }

    private record Option(String key, String comment, String defaultValue) {
        String text(String value) {
            return comment + key + " = " + value + "\n";
        }
    }
}

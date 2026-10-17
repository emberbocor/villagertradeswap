package io.github.emberbocor.villagertradeswap.config;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.MessageFormatter;

import com.electronwill.nightconfig.core.UnmodifiableConfig;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

final class ServerConfigReader {
    static final int MAX_REROLL_COST = 64;
    private static final Set<String> KEYS = Set.of(ServerConfigFile.REROLL_COST, ServerConfigFile.REROLL_CURRENCY, ServerConfigFile.ALLOW_SAME_TRADE_IN_LEVEL);

    private final Path file;

    ServerConfigReader(Path file) {
        this.file = file;
    }

    ServerConfig.Settings read(@Nullable UnmodifiableConfig config) {
        ServerConfig.Settings defaults = ServerConfig.DEFAULTS;
        if (config == null) {
            return defaults;
        }
        for (UnmodifiableConfig.Entry entry : config.entrySet()) {
            if (!KEYS.contains(entry.getKey())) {
                warn("unknown option '{}', ignored", entry.getKey());
            }
        }
        return new ServerConfig.Settings(
                rerollCost(config.get(ServerConfigFile.REROLL_COST), defaults.rerollCost()),
                rerollCurrency(config.get(ServerConfigFile.REROLL_CURRENCY), defaults.rerollCurrency()),
                flag(config.get(ServerConfigFile.ALLOW_SAME_TRADE_IN_LEVEL), ServerConfigFile.ALLOW_SAME_TRADE_IN_LEVEL, defaults.allowSameTradeInLevel()));
    }

    private int rerollCost(@Nullable Object value, int fallback) {
        if (value == null) {
            return fallback;
        }
        if ((value instanceof Integer || value instanceof Long) && ((Number) value).longValue() >= 0 && ((Number) value).longValue() <= MAX_REROLL_COST) {
            return ((Number) value).intValue();
        }
        warn("{} must be a whole number from 0 to {}, got '{}'; using {}", ServerConfigFile.REROLL_COST, MAX_REROLL_COST, value, fallback);
        return fallback;
    }

    private Item rerollCurrency(@Nullable Object value, Item fallback) {
        if (value == null) {
            return fallback;
        }
        String fallbackId = BuiltInRegistries.ITEM.getKey(fallback).toString();
        if (!(value instanceof String text)) {
            warn("{} must be an item id in quotes, got '{}'; using \"{}\"", ServerConfigFile.REROLL_CURRENCY, value, fallbackId);
            return fallback;
        }
        if (text.startsWith("#")) {
            warn("{} must be a single item id, item tags like \"{}\" are not supported; using \"{}\"", ServerConfigFile.REROLL_CURRENCY, text, fallbackId);
            return fallback;
        }
        Identifier id = Identifier.tryParse(text);
        Optional<Item> item = id != null ? BuiltInRegistries.ITEM.getOptional(id) : Optional.empty();
        if (item.isEmpty()) {
            warn("{} \"{}\" is not a known item id; using \"{}\"", ServerConfigFile.REROLL_CURRENCY, text, fallbackId);
            return fallback;
        }
        if (item.get() == Items.AIR) {
            warn("{} cannot be air; using \"{}\"", ServerConfigFile.REROLL_CURRENCY, fallbackId);
            return fallback;
        }
        return item.get();
    }

    private boolean flag(@Nullable Object value, String key, boolean fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Boolean flag) {
            return flag;
        }
        warn("{} must be true or false, got '{}'; using {}", key, value, fallback);
        return fallback;
    }

    private void warn(String message, Object... args) {
        VillagerTradeSwap.LOGGER.warn("{}: {}", file, MessageFormatter.arrayFormat(message, args).getMessage());
    }
}

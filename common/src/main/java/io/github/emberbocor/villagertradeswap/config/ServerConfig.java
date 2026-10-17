package io.github.emberbocor.villagertradeswap.config;

import java.nio.file.Path;

import io.github.emberbocor.villagertradeswap.platform.Services;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class ServerConfig {
    static final Settings DEFAULTS = new Settings(1, Items.EMERALD, true);

    private static volatile Settings settings = DEFAULTS;

    private ServerConfig() {
    }

    public static void load() {
        Path directory = Services.PLATFORM.getConfigDirectory();
        Path file = directory.resolve(ServerConfigFile.FILE_NAME);
        settings = new ServerConfigReader(file).read(ServerConfigFile.load(file, directory.resolve(LegacyServerConfig.FILE_NAME)).orElse(null));
    }

    public static int rerollCost() {
        return settings.rerollCost();
    }

    public static Item rerollCurrency() {
        return settings.rerollCurrency();
    }

    public static boolean allowSameTradeInLevel() {
        return settings.allowSameTradeInLevel();
    }

    record Settings(int rerollCost, Item rerollCurrency, boolean allowSameTradeInLevel) {
    }
}

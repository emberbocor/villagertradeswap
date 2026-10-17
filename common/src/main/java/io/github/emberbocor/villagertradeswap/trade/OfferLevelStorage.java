package io.github.emberbocor.villagertradeswap.trade;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class OfferLevelStorage {
    private static final String NBT_KEY = VillagerTradeSwap.MODID + ":offer_levels";

    private OfferLevelStorage() {
    }

    public static void save(ValueOutput output, @Nullable int[] levels) {
        if (levels != null && levels.length > 0) {
            output.putIntArray(NBT_KEY, levels);
        }
    }

    @Nullable
    public static int[] load(ValueInput input) {
        return input.getIntArray(NBT_KEY).orElse(null);
    }
}

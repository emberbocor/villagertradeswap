package io.github.emberbocor.villagertradeswap.trade;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class OfferLevelStorage {
    private static final String NBT_KEY = VillagerTradeSwap.MODID + ":offer_levels";

    private OfferLevelStorage() {
    }

    public static void save(CompoundTag tag, @Nullable int[] levels) {
        if (levels != null && levels.length > 0) {
            tag.putIntArray(NBT_KEY, levels);
        }
    }

    @Nullable
    public static int[] load(CompoundTag tag) {
        return tag.contains(NBT_KEY, Tag.TAG_INT_ARRAY) ? tag.getIntArray(NBT_KEY) : null;
    }
}

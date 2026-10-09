package io.github.emberbocor.villagertradeswap.trade;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.trading.MerchantOffer;

public final class OfferLevelTracker {
    private final Map<MerchantOffer, Integer> levels = new IdentityHashMap<>();
    @Nullable
    private int[] pending;

    public void load(@Nullable int[] levels, @Nullable List<MerchantOffer> offers) {
        pending = levels;
        bindPending(offers);
    }

    public int[] resolve(List<MerchantOffer> offers, int villagerLevel) {
        update(offers, index -> guess(index, villagerLevel));
        return offers.stream().mapToInt(levels::get).toArray();
    }

    public void assignUnknown(List<MerchantOffer> offers, int level) {
        update(offers, index -> level);
    }

    private void update(List<MerchantOffer> offers, IntUnaryOperator unknownLevel) {
        bindPending(offers);
        Map<MerchantOffer, Integer> updated = new IdentityHashMap<>();
        for (int i = 0; i < offers.size(); i++) {
            MerchantOffer offer = offers.get(i);
            Integer level = levels.get(offer);
            updated.put(offer, level != null ? level : unknownLevel.applyAsInt(i));
        }
        levels.clear();
        levels.putAll(updated);
    }

    private void bindPending(@Nullable List<MerchantOffer> offers) {
        if (pending == null || offers == null) {
            return;
        }
        if (pending.length == offers.size()) {
            levels.clear();
            for (int i = 0; i < pending.length; i++) {
                levels.put(offers.get(i), pending[i]);
            }
        }
        pending = null;
    }

    private static int guess(int index, int villagerLevel) {
        return Math.min(index / 2 + 1, Math.max(villagerLevel, 1));
    }
}

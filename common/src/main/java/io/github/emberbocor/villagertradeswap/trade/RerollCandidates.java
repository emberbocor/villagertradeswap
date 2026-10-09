package io.github.emberbocor.villagertradeswap.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.trading.MerchantOffer;

final class RerollCandidates {
    private RerollCandidates() {
    }

    @Nullable
    static MerchantOffer roll(Villager villager, List<MerchantOffer> offers, int[] levels, int index) {
        VillagerTrades.ItemListing[] listings = listings(villager, levels[index]);
        if (listings == null) {
            return null;
        }
        Set<TradeKind> takenKinds = IntStream.range(0, offers.size())
                .filter(i -> i != index && levels[i] == levels[index])
                .mapToObj(i -> TradeKind.of(offers.get(i)))
                .collect(Collectors.toSet());
        RandomSource random = villager.getRandom();
        List<VillagerTrades.ItemListing> remaining = new ArrayList<>(Arrays.asList(listings));
        while (!remaining.isEmpty()) {
            VillagerTrades.ItemListing listing = unwrap(villager, remaining.remove(random.nextInt(remaining.size())));
            if (listing == null) {
                continue;
            }
            MerchantOffer offer = MapTrades.isMapTrade(listing)
                    ? MapTrades.createOffer(villager, listing, takenKinds)
                    : listing.getOffer(villager, random);
            if (offer != null && !takenKinds.contains(TradeKind.of(offer))) {
                return offer;
            }
        }
        return null;
    }

    @Nullable
    private static VillagerTrades.ItemListing[] listings(Villager villager, int level) {
        VillagerProfession profession = villager.getVillagerData().getProfession();
        Int2ObjectMap<VillagerTrades.ItemListing[]> pool = null;
        if (villager.level().enabledFeatures().contains(FeatureFlags.TRADE_REBALANCE)) {
            pool = VillagerTrades.EXPERIMENTAL_TRADES.get(profession);
        }
        if (pool == null) {
            pool = VillagerTrades.TRADES.get(profession);
        }
        return pool != null ? pool.get(level) : null;
    }

    @Nullable
    private static VillagerTrades.ItemListing unwrap(Villager villager, VillagerTrades.ItemListing listing) {
        return listing instanceof VillagerTrades.TypeSpecificTrade typeSpecific
                ? typeSpecific.trades().get(villager.getVillagerData().getType())
                : listing;
    }
}

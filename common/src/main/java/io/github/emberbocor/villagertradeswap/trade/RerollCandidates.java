package io.github.emberbocor.villagertradeswap.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.trading.MerchantOffer;

final class RerollCandidates {
    private RerollCandidates() {
    }

    @Nullable
    static MerchantOffer roll(ServerLevel level, Villager villager, List<MerchantOffer> offers, int[] levels, int index, boolean allowSameKindInLevel) {
        VillagerTrades.ItemListing[] listings = listings(villager, levels[index]);
        if (listings == null) {
            return null;
        }
        List<TradeIdentity> takenTrades = offers.stream().map(TradeIdentity::of).toList();
        Set<TradeKind> takenKinds = allowSameKindInLevel ? Set.of() : IntStream.range(0, offers.size())
                .filter(i -> i != index && levels[i] == levels[index])
                .mapToObj(i -> TradeKind.of(offers.get(i)))
                .collect(Collectors.toSet());
        Predicate<TradeIdentity> allowed = trade -> !takenTrades.contains(trade) && !takenKinds.contains(trade.kind());

        RandomSource random = villager.getRandom();
        List<VillagerTrades.ItemListing> remaining = new ArrayList<>(Arrays.asList(listings));
        while (!remaining.isEmpty()) {
            VillagerTrades.ItemListing listing = unwrap(villager, remaining.remove(random.nextInt(remaining.size())));
            if (listing == null) {
                continue;
            }
            MerchantOffer offer = MapTrades.isMapTrade(listing)
                    ? MapTrades.createOffer(level, villager, listing, allowed)
                    : listing.getOffer(level, villager, random);
            if (offer != null && allowed.test(TradeIdentity.of(offer))) {
                return offer;
            }
        }
        return null;
    }

    @Nullable
    private static VillagerTrades.ItemListing[] listings(Villager villager, int level) {
        ResourceKey<VillagerProfession> profession = villager.getVillagerData().profession().unwrapKey().orElse(null);
        if (profession == null) {
            return null;
        }
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
                ? typeSpecific.trades().get(villager.getVillagerData().type().unwrapKey().orElse(null))
                : listing;
    }
}

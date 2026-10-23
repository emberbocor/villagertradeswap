package io.github.emberbocor.villagertradeswap.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

final class RerollCandidates {
    private RerollCandidates() {
    }

    @Nullable
    static MerchantOffer roll(ServerLevel level, Villager villager, List<MerchantOffer> offers, int[] levels, int index, boolean allowSameKindInLevel) {
        TradeSet tradeSet = tradeSet(level, villager, levels[index]);
        if (tradeSet == null) {
            return null;
        }
        List<TradeIdentity> takenTrades = offers.stream().map(offer -> TradeIdentity.of(offer, level.registryAccess())).toList();
        Set<TradeKind> takenKinds = allowSameKindInLevel ? Set.of() : IntStream.range(0, offers.size())
                .filter(i -> i != index && levels[i] == levels[index])
                .mapToObj(i -> TradeKind.of(offers.get(i)))
                .collect(Collectors.toSet());
        Predicate<TradeIdentity> allowed = trade -> !takenTrades.contains(trade) && !takenKinds.contains(trade.kind());

        RandomSource random = villager.getRandom();
        LootContext context = lootContext(level, villager, random);
        List<Holder<VillagerTrade>> remaining = new ArrayList<>(tradeSet.trades().stream().toList());
        while (!remaining.isEmpty()) {
            VillagerTrade trade = remaining.remove(random.nextInt(remaining.size())).value();
            MerchantOffer offer = MapTrades.deferMaps(() -> trade.getOffer(context));
            if (offer != null && allowed.test(TradeIdentity.of(offer, level.registryAccess()))) {
                MerchantOffer resolved = MapTrades.resolve(level, villager, offer);
                if (resolved != null) {
                    return resolved;
                }
            }
        }
        return null;
    }

    @Nullable
    private static TradeSet tradeSet(ServerLevel level, Villager villager, int villagerLevel) {
        ResourceKey<TradeSet> key = villager.getVillagerData().profession().value().getTrades(villagerLevel);
        return key != null ? level.registryAccess().lookupOrThrow(Registries.TRADE_SET).getValue(key) : null;
    }

    private static LootContext lootContext(ServerLevel level, Villager villager, RandomSource random) {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, villager.position())
                .withParameter(LootContextParams.THIS_ENTITY, villager)
                .withParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED, Unit.INSTANCE)
                .create(LootContextParamSets.VILLAGER_TRADE);
        return new LootContext.Builder(params).withOptionalRandomSource(random).create(Optional.empty());
    }
}

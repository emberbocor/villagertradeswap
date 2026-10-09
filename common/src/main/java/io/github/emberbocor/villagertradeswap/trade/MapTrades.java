package io.github.emberbocor.villagertradeswap.trade;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradeswap.mixin.TreasureMapForEmeraldsAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

final class MapTrades {
    private static final int SEARCH_RADIUS = 100;
    private static final TradeKind KIND = new TradeKind(Items.EMERALD, Items.COMPASS, Items.FILLED_MAP);
    private static final Map<ServerLevel, Map<SearchKey, Optional<BlockPos>>> STRUCTURE_CACHE = new WeakHashMap<>();

    private MapTrades() {
    }

    static boolean isMapTrade(VillagerTrades.ItemListing listing) {
        return listing.getClass() == VillagerTrades.TreasureMapForEmeralds.class;
    }

    @Nullable
    static MerchantOffer createOffer(Villager villager, VillagerTrades.ItemListing listing, Set<TradeKind> takenKinds) {
        if (takenKinds.contains(KIND) || !(villager.level() instanceof ServerLevel level)) {
            return null;
        }
        TreasureMapForEmeraldsAccessor map = (TreasureMapForEmeraldsAccessor) listing;
        BlockPos target = locate(level, map.villagertradeswap$getDestination(), villager.blockPosition());
        if (target == null) {
            return null;
        }
        ItemStack stack = MapItem.create(level, target.getX(), target.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(level, stack);
        MapItemSavedData.addTargetDecoration(stack, target, "+", map.villagertradeswap$getDestinationType());
        stack.set(DataComponents.ITEM_NAME, Component.translatable(map.villagertradeswap$getDisplayName()));
        return new MerchantOffer(new ItemCost(Items.EMERALD, map.villagertradeswap$getEmeraldCost()), Optional.of(new ItemCost(Items.COMPASS)),
                stack, map.villagertradeswap$getMaxUses(), map.villagertradeswap$getVillagerXp(), 0.2F);
    }

    @Nullable
    private static BlockPos locate(ServerLevel level, TagKey<Structure> destination, BlockPos origin) {
        return STRUCTURE_CACHE.computeIfAbsent(level, key -> new HashMap<>())
                .computeIfAbsent(new SearchKey(destination, new ChunkPos(origin)),
                        key -> Optional.ofNullable(level.findNearestMapStructure(destination, origin, SEARCH_RADIUS, true)))
                .orElse(null);
    }

    private record SearchKey(TagKey<Structure> destination, ChunkPos chunk) {
    }
}
